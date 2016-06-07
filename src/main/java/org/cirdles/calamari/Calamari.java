/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.calamari;

import java.io.IOException;
import javax.xml.bind.JAXBException;
import org.cirdles.calamari.core.RawDataFileHandler;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class Calamari {

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
            java.util.logging.Logger.getLogger(org.cirdles.calamari.userInterface.CalamariUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(org.cirdles.calamari.userInterface.CalamariUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(org.cirdles.calamari.userInterface.CalamariUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(org.cirdles.calamari.userInterface.CalamariUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        try {
            RawDataFileHandler.writeReportsFromPrawnFile("/Users/sbowring/Google Drive/_ETRedux_ProjectData/SHRIMP/100142_G6147_10111109.43.xml");
        } catch (IOException | JAXBException exception) {
            System.out.println("Exception extracting data: " + exception.getStackTrace()[0].toString());
        }
//        if (args.length > 0) {
//            System.out.println("Command line mode");
//            try {
//                RawDataFileHandler.writeReportsFromPrawnFile(args[0]);
//            } catch (IOException | JAXBException exception) {
//                System.out.println("Exception extracting data: " + exception.getStackTrace()[0].toString());
//            }
//        } else {
//            /* Create and display the form */
//            java.awt.EventQueue.invokeLater(() -> {
//                new org.cirdles.calamari.userInterface.CalamariUI().setVisible(true);
//            });
//        }
    }
}
