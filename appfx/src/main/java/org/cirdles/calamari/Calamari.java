/*
 * Copyright 2017 cirdles.org.
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
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.cirdles.calamari.core.CalamariReportsEngine;
import org.cirdles.calamari.core.PrawnFileHandler;
import org.cirdles.calamari.prawn.PrawnFile;
import org.cirdles.calamari.utilities.FileUtilities;
import org.cirdles.commons.util.ResourceExtractor;

/**
 *
 * @author bowring
 */
public class Calamari extends Application {

    private static PrawnFileHandler prawnFileHandler;

    @Override
    public void start(Stage primaryStage) throws Exception {

        initCalamari();

        primaryStage.setTitle("Squid 3.0 Explorations");

        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();

        //primaryStage.setOnCloseRequest(e -> Platform.exit());
        primaryStage.setOnCloseRequest((WindowEvent e) -> {
            Platform.exit();
            System.exit(0);
        });

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void initCalamari() {
        ResourceExtractor prawnFileResourceExtractor
                = new ResourceExtractor(PrawnFile.class);
        prawnFileHandler = new PrawnFileHandler();

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

        File defaultCalamariReportsFolder = new File("CalamariReports_v" + PrawnFileHandler.VERSION);
        prawnFileHandler.getReportsEngine().setFolderToWriteCalamariReports(defaultCalamariReportsFolder);
        if (!defaultCalamariReportsFolder.exists()) {
            if (!defaultCalamariReportsFolder.mkdir()) {
                System.out.println("Failed to make Calamari reports directory");
            }
        }
    }

    /**
     * @return the prawnFileHandler
     */
    public static PrawnFileHandler getPrawnFileHandler() {
        return prawnFileHandler;
    }

    public static File selectPrawnFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Prawn XML file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Prawn XML files", "*.xml"));
        fileChooser.setInitialDirectory(Calamari.getPrawnFileHandler().currentPrawnFileLocationFolder());
        File prawnFile = fileChooser.showOpenDialog(null);

        if (prawnFile != null) {
            try {
                Calamari.getPrawnFileHandler().setCurrentPrawnFileLocation(prawnFile.getCanonicalPath());
            } catch (IOException iOException) {
            }
        }

        return prawnFile;
    }

    public static File selectCalamariReportsLocation() {
        CalamariReportsEngine reportsEngine = Calamari.getPrawnFileHandler().getReportsEngine();

        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select Location for CalamariReports Folder");
        dirChooser.setInitialDirectory(reportsEngine.getFolderToWriteCalamariReports());
        File reportFolder = dirChooser.showDialog(null);

        if (reportFolder != null) {
            reportsEngine.setFolderToWriteCalamariReports(reportFolder);
        }

        return reportFolder;
    }
}
