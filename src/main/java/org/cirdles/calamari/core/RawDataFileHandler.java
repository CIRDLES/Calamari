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

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author James F. Bowring &lt;bowring at gmail.com&gt;
 */
@Deprecated
public class RawDataFileHandler {

    private static final PrawnFileHandler PRAWN_FILE_HANDLER = new PrawnFileHandler();

    /**
     *
     * @param prawnFileLocation the value of prawnFileLocation
     * @param useSBM the value of useSBM
     * @param userLinFits the value of userLinFits
     * @return
     * @throws MalformedURLException
     * @throws JAXBException
     */
    @Deprecated
    public static List<ShrimpFraction> extractShrimpFractionsFromPrawnFile(String prawnFileLocation, boolean useSBM, boolean userLinFits)
            throws MalformedURLException, JAXBException {
        return PRAWN_FILE_HANDLER.extractShrimpFractionsFromPrawnFile(prawnFileLocation, useSBM, userLinFits);
    }

    /**
     *
     * @param prawnFileLocation the value of prawnFileLocation
     * @param useSBM the value of useSBM
     * @param userLinFits the value of userLinFits
     * @throws IOException
     * @throws MalformedURLException
     * @throws JAXBException
     */
    @Deprecated
    public static void writeReportsFromPrawnFile(String prawnFileLocation, boolean useSBM, boolean userLinFits)
            throws IOException, MalformedURLException, JAXBException {
        PRAWN_FILE_HANDLER.writeReportsFromPrawnFile(prawnFileLocation, useSBM, userLinFits);
    }

    /**
     * @return the currentPrawnFileLocation
     */
    @Deprecated
    public static String getCurrentPrawnFileLocation() {
        return PRAWN_FILE_HANDLER.getCurrentPrawnFileLocation();
    }

    /**
     * @param aCurrentPrawnFileLocation the currentPrawnFileLocation to set
     */
    @Deprecated
    public static void setCurrentPrawnFileLocation(String aCurrentPrawnFileLocation) {
        PRAWN_FILE_HANDLER.setCurrentPrawnFileLocation(aCurrentPrawnFileLocation);
    }

    @Deprecated
    public static void setProgressSubscriber(Consumer<Integer> progressSubscriber) {
        PRAWN_FILE_HANDLER.setProgressSubscriber(progressSubscriber);
    }

}
