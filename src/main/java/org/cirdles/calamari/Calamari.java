/*
 * Copyright 2006-2016 CIRDLES.org.
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.cirdles.calamari.core.RawDataFileHandler;
import org.cirdles.calamari.prawn.PrawnFile;
import org.cirdles.commons.util.ResourceExtractor;


/**
 *
 * @author James F. Bowring &lt;bowring at gmail.com&gt;
 */
public class Calamari {

    public static String VERSION = "version";

    public static String RELEASE_DATE = "date";

    private static ResourceExtractor RESOURCE_EXTRACTOR
            = new ResourceExtractor(Calamari.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        // get version number and release date written by pom.xml
        Path resourcePath = RESOURCE_EXTRACTOR.extractResourceAsPath("version.txt");
        Charset charset = Charset.forName("US-ASCII");
        try (BufferedReader reader = Files.newBufferedReader(resourcePath, charset)) {

            String[] versionText = reader.readLine().split("=");
            VERSION = versionText[1];

            String[] versionDate = reader.readLine().split("=");
            RELEASE_DATE = versionDate[1];

            reader.close();
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

        RESOURCE_EXTRACTOR = new ResourceExtractor(PrawnFile.class);

        Path listOfPrawnFiles = RESOURCE_EXTRACTOR.extractResourceAsPath("listOfPrawnFiles.txt");
        if (listOfPrawnFiles != null) {
            List<File> prawnFiles = new ArrayList<>();
            try {
                List<String> fileNames = Files.readAllLines(listOfPrawnFiles, ISO_8859_1);
                for (int i = 0; i < fileNames.size(); i++) {
                    // test for empty string
                    if (fileNames.get(i).trim().length() > 0) {
                        File prawnFileResource = RESOURCE_EXTRACTOR.extractResourceAsFile(fileNames.get(i));
                        File exampleFolder = new File("ExamplePrawnFiles");
                        exampleFolder.mkdir();
                        File prawnFile = new File(exampleFolder.getCanonicalPath() + File.separator + fileNames.get(i));
                        System.out.println("PrawnFile added: " + fileNames.get(i));
                        prawnFileResource.renameTo(prawnFile);
                        prawnFiles.add(prawnFile);
                    }
                }

                RawDataFileHandler.setCurrentPrawnFileLocation(prawnFiles.get(0).getCanonicalPath());
            } catch (IOException iOException) {
            }
        }

        /* Set the Metal look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Metal is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Motif".equals(info.getName())) { //Nimbus (original), Motif, Metal
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
                RawDataFileHandler.writeReportsFromPrawnFile(args[0], Boolean.valueOf(args[1]), Boolean.valueOf(args[2]));
            } catch (IOException | JAXBException exception) {
                System.out.println("Exception extracting data: " + exception.getStackTrace()[0].toString());
            }
        } else {
            /* Create and display the form */
            java.awt.EventQueue.invokeLater(() -> {
                new org.cirdles.calamari.userInterface.CalamariUI().setVisible(true);
            });
        }
    }
}
