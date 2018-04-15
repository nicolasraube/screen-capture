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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class SaveWindow extends javax.swing.JFrame {

    private BufferedImage screenshot;
    private JFrame window;

    public SaveWindow(BufferedImage screenshot) {
        setContentPane(new SavePanel(screenshot));

        initComponents();

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        this.screenshot = screenshot;
    }
    
    public SaveWindow(BufferedImage screenshot, JFrame window) {
        setContentPane(new SavePanel(screenshot));

        initComponents();

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        this.screenshot = screenshot;
        this.window = window;
    }

    private void initComponents() {

        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancel)
                .addGap(0, 706, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(508, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pack();
    }

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {
        if (saveScreenshot()) {
            dispose();
            window.dispose();
        }
    }

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
        window.dispose();
    }

    private boolean saveScreenshot() {

        String savePath = showSaveDialog();
        if (savePath != null) {

            try {
                ImageIO.write(screenshot, "PNG", new File(savePath));
            } catch (IOException ex) {
                Logger.getLogger(SaveWindow.class.getName()).log(Level.SEVERE, null, ex);
            }

            return true;

        }

        return false;

    }

    private String showSaveDialog() {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getPath();
            if (!path.endsWith(".png") && !path.endsWith(".jpg")) {
                path = path + ".png";
            }
            return path;
        }

        return null;

    }

    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnCancel;
}
