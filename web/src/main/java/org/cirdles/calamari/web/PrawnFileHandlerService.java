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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBException;
import org.cirdles.calamari.core.CalamariReportsEngine;
import org.cirdles.calamari.core.PrawnFileHandler;

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
            InputStream prawnFile,
            boolean useSBM,
            boolean userLinFits) throws IOException, JAXBException {
        
        Path uploadDirectory = Files.createTempDirectory("upload");
        Path prawnFilePath = uploadDirectory.resolve("prawn-file.xml");
        Files.copy(prawnFile, prawnFilePath);
        
        Path reportsDestination = Files.createTempDirectory("reports-destination");
        reportsEngine.setFolderToWriteCalamariReports(reportsDestination.toFile());
        
        prawnFileHandler.initReportsEngineWithCurrentPrawnFileName();
        
        prawnFileHandler.writeReportsFromPrawnFile(
                prawnFilePath.toString(),
                useSBM,
                userLinFits);
        
        Files.delete(prawnFilePath);
        
        Path dest = null;
        try {
            dest = Paths.get(new URI(reportsDestination.toFile().listFiles()[0].listFiles()[0].getCanonicalPath()));
            System.out.println("<<<<" + dest.toFile().getCanonicalPath());
        } catch (IOException | URISyntaxException iOException) {
            System.out.println("<<<<" + "NADA");
        }
        
        System.out.println(">>>>" + reportsDestination);

        Path reports = Files.list(dest)
                .findFirst().orElseThrow(() -> new IllegalStateException());
        
        Path reportsZip = zip(reports);
        recursiveDelete(reports);
        
        return reportsZip;
    }
    
}
