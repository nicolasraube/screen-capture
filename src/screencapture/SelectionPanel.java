/**
 *
 * @author Nicolas 10.02.2016
 *
Screen Capture is a tool for taking screenshots.
Copyright (C) 2016  Nicolas Raube

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package screencapture;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class SelectionPanel extends JPanel {

    private SelectionWindow window;
    private final int REPAINT_RATE = 25;

    private Point point1, point2;

    private boolean drawZoom;
    private Point zoomPoint1, zoomPoint2, zoomPoint3, zoomPoint4;
    private int zoomWidth = 200, zoomHeight = 200;
    private int zoomOffsetX = 25, zoomOffsetY = 25;

    public SelectionPanel(SelectionWindow window) {
        this.window = window;

        startRepaintTimer();

        addInputListener();

        drawZoom = true;

        Toolkit tk = Toolkit.getDefaultToolkit();
        int screenWidth = (int) tk.getScreenSize().getWidth(), screenHeight = (int) tk.getScreenSize().getHeight();

        int zoomX1 = zoomOffsetX;
        int zoomY1 = zoomOffsetY;
        int zoomX2 = screenWidth - zoomWidth - zoomOffsetX;
        int zoomY2 = zoomOffsetY;
        int zoomX3 = zoomOffsetX;
        int zoomY3 = screenHeight - zoomHeight - zoomOffsetY;
        int zoomX4 = screenWidth - zoomWidth - zoomOffsetX;
        int zoomY4 = screenHeight - zoomHeight - zoomOffsetY;

        zoomPoint1 = new Point(zoomX1, zoomY1);
        zoomPoint2 = new Point(zoomX2, zoomY2);
        zoomPoint3 = new Point(zoomX3, zoomY3);
        zoomPoint4 = new Point(zoomX4, zoomY4);
    }

    private void addInputListener() {
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);

                int mouseX = (int) MouseInfo.getPointerInfo().getLocation().x;
                int mouseY = (int) MouseInfo.getPointerInfo().getLocation().y;

                if (me.getButton() == MouseEvent.BUTTON3) {
                    if (point1 != null) {
                        point1 = null;
                    } else {
                        window.dispose();
                    }
                } else if (me.getButton() == MouseEvent.BUTTON1) {

                    if (point1 == null) {

                        point1 = new Point(mouseX, mouseY);

                    } else if (point2 == null) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                point2 = new Point(mouseX, mouseY);
                                drawZoom = false;
                                repaint();

                                try {
                                    Thread.sleep(200);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(SelectionPanel.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                BufferedImage screenshot = takeScreenShot(point1.x + 1, point1.y + 1, point2.x - point1.x - 2, point2.y - point1.y - 2);
                                showSaveScreen(screenshot);
                            }
                        }).start();

                    }

                }
            }

        });
    }

    private void startRepaintTimer() {

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                repaint();
            }
        }, REPAINT_RATE, REPAINT_RATE);

    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (graphics instanceof Graphics2D) {
            final int R = 240;
            final int G = 240;
            final int B = 240;

            Paint p = new GradientPaint(0.0f, 0.0f, new Color(R, G, B,
                    0), 0.0f, getHeight(), new Color(R, G, B, 0),
                    true);
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.setPaint(p);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        int mouseX = MouseInfo.getPointerInfo().getLocation().x;
        int mouseY = MouseInfo.getPointerInfo().getLocation().y;

        graphics.setColor(Color.GREEN);

        graphics.drawLine(mouseX, 0, mouseX, (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
        graphics.drawLine(0, mouseY, (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), mouseY);

        graphics.setColor(Color.red);

        if (point1 != null) {

            Graphics2D g2d = (Graphics2D) graphics;
            Stroke oldStroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(2));
            
            g2d.drawRect(point1.x, point1.y, mouseX - point1.x, mouseY - point1.y);
            
            g2d.setStroke(oldStroke);

        }

        if (drawZoom) {

            Toolkit tk = Toolkit.getDefaultToolkit();

            int screenWidth = (int) tk.getScreenSize().getWidth();
            int screenHeight = (int) tk.getScreenSize().getHeight();

            Point finalZoomPoint = null;

            if (mouseX < screenWidth / 2 && mouseY < screenHeight / 2) {
                finalZoomPoint = zoomPoint4;
            } else if (mouseX > screenWidth / 2 && mouseY < screenHeight / 2) {
                finalZoomPoint = zoomPoint3;
            } else if (mouseX < screenWidth / 2 && mouseY > screenHeight / 2) {
                finalZoomPoint = zoomPoint2;
            } else if (mouseX > screenWidth / 2 && mouseY > screenHeight / 2) {
                finalZoomPoint = zoomPoint1;
            }

            BufferedImage zoom = takeScreenShot(mouseX - 25, mouseY - 25, 50, 50);
            graphics.drawImage(zoom, finalZoomPoint.x, finalZoomPoint.y, zoomWidth, zoomHeight, null);

        }
    }

    private BufferedImage takeScreenShot(int x, int y, int width, int height) {

        BufferedImage screenshot = null;

        try {
            screenshot = new Robot().createScreenCapture(new Rectangle(x, y, width, height));
        } catch (AWTException ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        }

        return screenshot;
    }

    private void showSaveScreen(BufferedImage screenshot) {

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new SaveWindow(screenshot, window).setVisible(true);
            }
        });

    }

}
