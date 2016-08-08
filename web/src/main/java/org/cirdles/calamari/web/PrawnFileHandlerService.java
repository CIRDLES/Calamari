/*
 * Copyright 2016 CIRDLES
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.calamari.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBException;
import org.cirdles.calamari.core.CalamariReportsEngine;
import org.cirdles.calamari.core.PrawnFileHandler;
import org.xml.sax.SAXException;

/**
 * Created by johnzeringue on 7/27/16.
 */
public class PrawnFileHandlerService {

    private static final Map<String, String> ZIP_FILE_ENV;

    static {
        Map<String, String> zipFileEnv = new HashMap<>();
        zipFileEnv.put("create", "true");

        ZIP_FILE_ENV = Collections.unmodifiableMap(zipFileEnv);
    }

    private final PrawnFileHandler prawnFileHandler;
    private final CalamariReportsEngine reportsEngine;

    public PrawnFileHandlerService() {
        prawnFileHandler = new PrawnFileHandler();
        reportsEngine = prawnFileHandler.getReportsEngine();
    }

    private Path zip(Path target) throws IOException {
        Path zipFilePath = target.resolveSibling("reports.zip");

        try (FileSystem zipFileFileSystem = FileSystems.newFileSystem(
                URI.create("jar:file:" + zipFilePath), ZIP_FILE_ENV)) {

            Files.list(target).forEach(entry -> {
                try {
                    Files.copy(entry, zipFileFileSystem.getPath("/" + entry.getFileName()));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }

        return zipFilePath;
    }

    private void unZip(Path zippedFilePath, Path target) throws IOException {
        Map<String, String> zip_properties = new HashMap<>();
        zip_properties.put("create", "false");

        URI zip_disk = URI.create("jar:file:" + zippedFilePath);

        try (FileSystem zipFileSystem = FileSystems.newFileSystem(zip_disk, zip_properties)) {
            final Path root = zipFileSystem.getPath("/");

            //walk the zip file tree and copy files to the destination
            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file,
                        BasicFileAttributes attrs) throws IOException {
                    final Path destFile = Paths.get(target.toString(),
                            file.toString());
                    //System.out.printf("Extracting file %s to %s\n", file, destFile);
                    Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    private void recursiveDelete(Path directory) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(
                    Path file,
                    BasicFileAttributes attrs) throws IOException {

                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(
                    Path dir,
                    IOException exc) throws IOException {

                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public Path generateReports(
            String fileName,
            InputStream prawnFile,
            boolean useSBM,
            boolean userLinFits) throws IOException, JAXBException, SAXException {

        Path uploadDirectory = Files.createTempDirectory("upload");
        Path prawnFilePath = uploadDirectory.resolve("prawn-file.xml");
        Files.copy(prawnFile, prawnFilePath);

        Path calamarirReportsFolderAlias = Files.createTempDirectory("reports-destination");
        File reportsDestinationFile = calamarirReportsFolderAlias.toFile();

        reportsEngine.setFolderToWriteCalamariReports(reportsDestinationFile);

        // this gives reportengine the name of the Prawnfile for use in report names
        prawnFileHandler.initReportsEngineWithCurrentPrawnFileName(fileName);//"100142_G6147_10111109.43.xml");

        prawnFileHandler.writeReportsFromPrawnFile(
                prawnFilePath.toString(),
                useSBM,
                userLinFits);

        Files.delete(prawnFilePath);

        Path reportsFolder = Paths.get(reportsEngine.getFolderToWriteCalamariReportsPath()).getParent();

        Path reports = Files.list(reportsFolder)
                .findFirst().orElseThrow(() -> new IllegalStateException());

        Path reportsZip = zip(reports);
        recursiveDelete(reports);

        return reportsZip;
    }

}
