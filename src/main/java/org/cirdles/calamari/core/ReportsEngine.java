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
package org.cirdles.calamari.core;

import org.cirdles.calamari.shrimp.ShrimpFraction;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author James F. Bowring &lt;bowring at gmail.com&gt;
 */
public class ReportsEngine {

    private static final CalamariReportsEngine CALAMARI_REPORTS_ENGINE
            = new CalamariReportsEngine();

    /**
     * ReportsEngine to test results
     *
     * @param shrimpFractions the value of shrimpFractions
     * @throws java.io.IOException
     */
    protected static void produceReports(List<ShrimpFraction> shrimpFractions) throws IOException {
        CALAMARI_REPORTS_ENGINE.produceReports(shrimpFractions);
    }

    /**
     * @return the folderToWriteCalamariReports
     */
    public static File getFolderToWriteCalamariReports() {
        return CALAMARI_REPORTS_ENGINE.getFolderToWriteCalamariReports();
    }

    /**
     * @param aFolderToWriteCalamariReports the folderToWriteCalamariReports to
     * set
     */
    public static void setFolderToWriteCalamariReports(File aFolderToWriteCalamariReports) {
        CALAMARI_REPORTS_ENGINE.setFolderToWriteCalamariReports(aFolderToWriteCalamariReports);
    }

}
