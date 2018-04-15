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

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class SavePanel extends JPanel {
    
    private BufferedImage screenshot;
    
    public SavePanel(BufferedImage screenshot) {
        this.screenshot = screenshot;
    }
    
    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        
        graphics.drawImage(screenshot, 0, 0, screenshot.getWidth(), screenshot.getHeight(), this);
    }
    
}
