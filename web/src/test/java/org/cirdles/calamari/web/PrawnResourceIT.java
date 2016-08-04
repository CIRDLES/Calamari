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
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import static org.assertj.core.api.Assertions.assertThat;
import org.cirdles.commons.util.ResourceExtractor;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Rule;
import org.junit.rules.Timeout;

/**
 * Created by johnzeringue on 7/27/16.
 */
public class PrawnResourceIT extends JerseyTest {

    private static final String PRAWN_FILE_RESOURCE
            = "/org/cirdles/calamari/prawn/100142_G6147_10111109.43.xml";

    private static final ResourceExtractor RESOURCE_EXTRACTOR
            = new ResourceExtractor(PrawnResourceIT.class);

    @Rule
    public Timeout timeout = Timeout.seconds(120);

    @Override
    protected Application configure() {
        ResourceConfig resourceConfig = new ResourceConfig(PrawnResource.class);
        resourceConfig.register(MultiPartFeature.class);

        return resourceConfig;
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(MultiPartFeature.class);
    }

    //@Test
    public void testGenerateReports() throws IOException {
        Path prawnFilePath = RESOURCE_EXTRACTOR
                .extractResourceAsPath(PRAWN_FILE_RESOURCE);

        FormDataMultiPart multiPart = new FormDataMultiPart()
                .field("prawnFile", prawnFilePath.toFile(), MediaType.APPLICATION_XML_TYPE);

        Path reportsZip = target("prawn")
                .request("application/zip")
                .accept(MediaType.MULTIPART_FORM_DATA)
                .post(Entity.entity(multiPart, multiPart.getMediaType()))
                .readEntity(File.class)
                .toPath();

        String newFileName = reportsZip.getFileName().toString() + ".zip";
        Path newReportsZip = reportsZip.resolveSibling(newFileName);
        Files.move(reportsZip, newReportsZip);

        Path reportsZipRoot = FileSystems
                .newFileSystem(newReportsZip, null)
                .getPath("/");

        Files.list(reportsZipRoot).forEach(report -> {
            Path expectedReport = RESOURCE_EXTRACTOR.extractResourceAsPath(
                    "/org/cirdles/calamari/core/" + report.getFileName());

            assertThat(report).hasSameContentAs(expectedReport);
        });
    }

}
