/*
 * Copyright 2006-2017 CIRDLES.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.calamari;

import java.io.File;
import java.io.IOException;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.cirdles.calamari.core.PrawnFileHandler;
import org.cirdles.calamari.prawn.PrawnFile;
import org.cirdles.calamari.userInterface.CalamariUI;
import org.cirdles.calamari.utilities.FileUtilities;
import org.cirdles.commons.util.ResourceExtractor;
import org.xml.sax.SAXException;

/**
 *
 * @author James F. Bowring
 */
public class Calamari {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // set up folder of example Prawn files
        ResourceExtractor prawnFileResourceExtractor
                = new ResourceExtractor(PrawnFile.class);

        PrawnFileHandler prawnFileHandler = new PrawnFileHandler();

        Path listOfPrawnFiles = prawnFileResourceExtractor.extractResourceAsPath("listOfPrawnFiles.txt");
        if (listOfPrawnFiles != null) {
            File exampleFolder = new File("ExamplePrawnXMLFiles");
            try {
                if (exampleFolder.exists()) {
                    FileUtilities.recursiveDelete(exampleFolder.toPath());
                }
                if (exampleFolder.mkdir()) {
                    List<String> fileNames = Files.readAllLines(listOfPrawnFiles, ISO_8859_1);
                    for (int i = 0; i < fileNames.size(); i++) {
                        // test for empty string
                        if (fileNames.get(i).trim().length() > 0) {
                            File prawnFileResource = prawnFileResourceExtractor.extractResourceAsFile(fileNames.get(i));
                            File prawnFile = new File(exampleFolder.getCanonicalPath() + File.separator + fileNames.get(i));

                            if (prawnFileResource.renameTo(prawnFile)) {
                                System.out.println("PrawnFile added: " + fileNames.get(i));
                            } else {
                                System.out.println("PrawnFile failed to add: " + fileNames.get(i));
                            }
                        }
                    }
                }
            } catch (IOException iOException) {
            }
            try {
                // point to directory, but no default choice
                prawnFileHandler.setCurrentPrawnFileLocation(exampleFolder.getCanonicalPath());
            } catch (IOException iOException) {
            }
        }

        // Set up default folder for reports
        File defaultCalamariReportsFolder = new File("CalamariReports_v" + PrawnFileHandler.VERSION);
        prawnFileHandler.getReportsEngine().setFolderToWriteCalamariReports(defaultCalamariReportsFolder);
        if (!defaultCalamariReportsFolder.exists()) {
            if (!defaultCalamariReportsFolder.mkdir()) {
                System.out.println("Failed to make Calamari reports directory");
            }
        }

        /* Set the Metal look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Metal is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) { //Nimbus (original), Motif, Metal
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(org.cirdles.calamari.userInterface.CalamariUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        if (args.length == 3) {// remove 4th argument from properties dialog command line arguments to get commandline
            System.out.println("Command line mode");
            try {
                prawnFileHandler.writeReportsFromPrawnFile(args[0], Boolean.valueOf(args[1]), Boolean.valueOf(args[2]), "T");
            } catch (IOException | JAXBException | SAXException exception) {
                System.out.println("Exception extracting data: " + exception.getStackTrace()[0].toString());
            }
        } else {
            /* Create and display the form */
            CalamariUI calamariUI = new org.cirdles.calamari.userInterface.CalamariUI(prawnFileHandler);
            
            java.awt.EventQueue.invokeLater(() -> {
               calamariUI.setVisible(true);                
            });
        }
    }
}
