/*
 * Copyright (C) 2018 MS
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package alphabet_generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author MS
 */
public class FilenameFrame extends javax.swing.JFrame {
    private final String CONFIG_NAME = "settings.conf";
    private final String PATH_KEY = "default_path";
    /**
     * Creates new form FilenameFrame
     */
    public FilenameFrame() {
        initComponents();
        readProperties();
        tryEnableOpen();
    }
    
    private void tryEnableOpen() {
        if (jTextDir.getText().length() > 0)
            jButtonOpen.setEnabled(true);
        else
            jButtonOpen.setEnabled(false);
    }
    
    private String getDefaultPath() {
        try {
        String path = FilenameFrame.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        return URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.toString());
            return "";
        }
    }
    
    private void readProperties() {
       try {
         Properties prop = new Properties();
         InputStream input = new FileInputStream(CONFIG_NAME);
         prop.load(input);
         input.close();
         jTextDir.setText(prop.getProperty(PATH_KEY));
       } catch (IOException e) {
           System.out.println(e.toString());
       }
    }
    private void writeProperties() {
        try {
            if (jTextDir.getText().length() == 0)
                throw new NullPointerException("Pusta ścieżka");
            OutputStream output = new FileOutputStream(CONFIG_NAME);
            Properties prop = new Properties();
            prop.setProperty(PATH_KEY, jTextDir.getText());
            prop.store(output, null);
            output.close();
        } catch (IOException | NullPointerException e) {
            System.out.println(e.toString());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupType = new javax.swing.ButtonGroup();
        jTextDir = new javax.swing.JTextField();
        jButtonSelect = new javax.swing.JButton();
        jButtonOpen = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Alphabet Generator - alphabet file");

        jTextDir.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jTextDirCaretUpdate(evt);
            }
        });

        jButtonSelect.setText("...");
        jButtonSelect.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButtonSelect.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonSelectMouseClicked(evt);
            }
        });

        jButtonOpen.setText("Open");
        jButtonOpen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonOpenMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonOpen)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextDir, javax.swing.GroupLayout.PREFERRED_SIZE, 513, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSelect)))
                .addGap(0, 10, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSelect))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonOpen)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSelectMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonSelectMouseClicked
        JFileChooser f = new JFileChooser();
        f.setFileSelectionMode(JFileChooser.FILES_ONLY); 
        f.setCurrentDirectory(new File(getDefaultPath()));
        f.showSaveDialog(null);
        jTextDir.setText(f.getSelectedFile().toString());
        
        

        //System.out.println(f.getCurrentDirectory().toString());
        //System.out.println(f.getSelectedFile().toString());
    }//GEN-LAST:event_jButtonSelectMouseClicked

    private void jTextDirCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTextDirCaretUpdate
        tryEnableOpen();
    }//GEN-LAST:event_jTextDirCaretUpdate

    private void jButtonOpenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonOpenMouseClicked
        if (jButtonOpen.isEnabled() && jTextDir.getText().length() > 0) {
            boolean open = false;
            File f = new File(jTextDir.getText());
            if (f.exists()) {
                if(ASCII_List.verifyCSV(jTextDir.getText())) {
                    open = true;
                }
            } else {
                try {
                    f.getParentFile().mkdirs(); 
                    f.createNewFile();
                } catch (IOException e) {
                    System.out.println(e.toString());
                }
                open = true;
            }
            if (open == true) {
                writeProperties();
                Main_Frame main = new Main_Frame(jTextDir.getText());
                main.setVisible(true);
                setVisible(false);
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Not supported file format", "File verification", JOptionPane.WARNING_MESSAGE);
            }
        } 
    }//GEN-LAST:event_jButtonOpenMouseClicked

    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FilenameFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FilenameFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FilenameFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FilenameFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FilenameFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupType;
    private javax.swing.JButton jButtonOpen;
    private javax.swing.JButton jButtonSelect;
    private javax.swing.JTextField jTextDir;
    // End of variables declaration//GEN-END:variables
}
