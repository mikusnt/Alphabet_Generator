/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabet_generator;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author MS-1
 */
public class Alphabet_Generator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /*for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            System.out.println(info.getClassName());
        }*/
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
                System.out.println(e.toString());
        }
        //Alphabet_Char first = new Alphabet_Char(5, new int[]{ 0, 1, 2, 3, 4});
        //System.out.println(first.toString());
        //Main_Frame frame = new Main_Frame();
        //frame.setVisible(true);
        Filename_Frame file = new Filename_Frame();
        file.setVisible(true);
        

    }
    
}
