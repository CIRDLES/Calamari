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

package org.cirdles.calamari.core;

import org.cirdles.commons.util.ResourceExtractor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class RawDataFileHandlerIT {

    private static final String PRAWN_FILE_RESOURCE
            = "/org/cirdles/calamari/prawn/100142_G6147_10111109.43.xml";

    private static final ResourceExtractor RESOURCE_EXTRACTOR
            = new ResourceExtractor(RawDataFileHandlerIT.class);

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public Timeout timeout = Timeout.seconds(60);

    @Test
    public void writesReportsFromPrawnFile() throws Exception {
        File reportsFolder = temporaryFolder.getRoot();
        ReportsEngine.setFolderToWriteCalamariReports(reportsFolder);

        File prawnFile = RESOURCE_EXTRACTOR
                .extractResourceAsFile(PRAWN_FILE_RESOURCE);

        RawDataFileHandler.writeReportsFromPrawnFile(
                prawnFile.getAbsolutePath(), // prawnFileLocation
                true,                        // useSBM
                false);                      // userLinFits

        assertThat(reportsFolder.listFiles()).hasSize(1);

        assertThat(reportsFolder.listFiles()[0])
                .isDirectory()
                .hasName("CalamariReports-G6147--SBM-TRUE--FIT-FALSE");

        for (File report : reportsFolder.listFiles()[0].listFiles()) {
            File expectedReport = RESOURCE_EXTRACTOR
                    .extractResourceAsFile(report.getName());

            assertThat(report).hasSameContentAs(expectedReport);
        }
    }

}
