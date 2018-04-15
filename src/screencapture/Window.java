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
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

public class Window extends javax.swing.JDialog {

    private boolean lockScreenShow = false;

    public Window() {
        setUndecorated(true);
        initComponents();

        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - getWidth() / 2, -getHeight());
        getContentPane().setBackground(Color.white);
        setAlwaysOnTop(true);

        startMouseTrackerTimer();
    }

    private void startMouseTrackerTimer() {

        final int CURSOR_TRACK_RATE = 500;

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                trackCursor();
            }
        }, CURSOR_TRACK_RATE, CURSOR_TRACK_RATE);

    }

    private void trackCursor() {

        Point cursor = getCursorLocation();

        if (!lockScreenShow && isCursorInWindowLocation(cursor.x, cursor.y)) {
            showWindow();
        } else if (!lockScreenShow) {
            hideWindow();
        }

    }

    private boolean isCursorInWindowLocation(int mouseX, int mouseY) {
        if (mouseX > getX() && mouseX < getX() + getWidth() && mouseY < getHeight()) {
            return true;
        }

        return false;
    }

    private void showWindow() {
        setLocation(getX(), 0);
    }

    private void hideWindow() {
        setLocation(getX(), -getHeight());
    }
    
    private void captureRectangleImage() {
        
        lockScreenShow = true;
        hideWindow();

        BufferedImage screenshot = takeScreenShot(0, 0, (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
        showSelectionWindow(screenshot);
        
    }
    
    public void makeWindowVisible() {
        lockScreenShow = false;
        showWindow();        
    }
    
    private void showSelectionWindow(BufferedImage screenshot) {
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SelectionWindow(Window.this).setVisible(true);
            }
        });
        
    }

    private void captureFullScreenImage() {

        lockScreenShow = true;
        hideWindow();

        Point oldCursorLoc = getCursorLocation();
        hideCursor();

        BufferedImage screenshot = takeScreenShot(0, 0, (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
        showCursorAt(oldCursorLoc);

        showSaveScreen(screenshot);
        lockScreenShow = false;
        showWindow();

    }

    private void hideCursor() {
        try {
            new Robot().mouseMove((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
        } catch (AWTException ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Point getCursorLocation() {
        return MouseInfo.getPointerInfo().getLocation();
    }

    private void showCursorAt(Point pos) {
        try {
            new Robot().mouseMove(pos.x, pos.y);
        } catch (AWTException ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
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
                new SaveWindow(screenshot).setVisible(true);
            }
        });
        
    }

    private void initComponents() {
        btnCaptureFullscreen = new javax.swing.JButton();
        btnCaptureSelection = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        btnCaptureFullscreen.setText("capture fullscreen");
        btnCaptureFullscreen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCaptureFullscreenActionPerformed(evt);
            }
        });

        btnCaptureSelection.setText("capture rectangle selection");
        btnCaptureSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCaptureSelectionActionPerformed(evt);
            }
        });

        btnClose.setText("close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(btnCaptureFullscreen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCaptureSelection)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnClose))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnCaptureFullscreen)
                .addComponent(btnCaptureSelection)
                .addComponent(btnClose))
        );

        pack();
    }

    private void btnCaptureFullscreenActionPerformed(java.awt.event.ActionEvent evt) {
        captureFullScreenImage();
    }

    private void btnCaptureSelectionActionPerformed(java.awt.event.ActionEvent evt) {
        captureRectangleImage();
    }

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {
        System.exit(0);
    }

    private javax.swing.JButton btnCaptureFullscreen;
    private javax.swing.JButton btnCaptureSelection;
    private javax.swing.JButton btnClose;
}
