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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.cirdles.calamari.prawn.PrawnFile;
import org.cirdles.calamari.prawn.PrawnRunFractionParser;
import org.cirdles.calamari.shrimp.ShrimpFraction;

/**
 *
 * @author James F. Bowring &lt;bowring at gmail.com&gt;
 */
public class RawDataFileHandler {

    private static JAXBContext jaxbContext;
    private static Unmarshaller jaxbUnmarshaller;
    private static String currentPrawnFileLocation = "https://raw.githubusercontent.com/bowring/XSD/master/SHRIMP/EXAMPLE_100142_G6147_10111109.43_10.33.37%20AM.xml";
    //"/Users/sbowring/Google Drive/_ETRedux_ProjectData/SHRIMP/100142_G6147_10111109.43.xml"

    private static Consumer<Integer> progressSubscriber;

    /**
     *
     * @param prawnFileLocation the value of prawnFileLocation
     * @param useSBM the value of useSBM
     * @param userLinFits the value of userLinFits
     * @return 
     * @throws MalformedURLException
     * @throws JAXBException
     */
    public static List<ShrimpFraction> extractShrimpFractionsFromPrawnFile(String prawnFileLocation, boolean useSBM, boolean userLinFits)
            throws MalformedURLException, JAXBException {
        PrawnFile prawnFile = unmarshallRawDataXML(prawnFileLocation);
        String nameOfMount = prawnFile.getMount();
        List<ShrimpFraction> shrimpFractions = new ArrayList<>();

        for (int f = 0; f < prawnFile.getRuns(); f++) {
            PrawnFile.Run runFraction = prawnFile.getRun().get(f);
            ShrimpFraction shrimpFraction = PrawnRunFractionParser.processRunFraction(runFraction, useSBM, userLinFits);
            shrimpFraction.setSpotNumber(f + 1);
            shrimpFraction.setNameOfMount(nameOfMount);
            shrimpFractions.add(shrimpFraction);

            if (progressSubscriber != null) {
                int progress = (f + 1) * 100 / prawnFile.getRuns();
                progressSubscriber.accept(progress);
            }
        }

        return shrimpFractions;
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
    public static void writeReportsFromPrawnFile(String prawnFileLocation, boolean useSBM, boolean userLinFits)
            throws IOException, MalformedURLException, JAXBException {
        List<ShrimpFraction> shrimpFractions = extractShrimpFractionsFromPrawnFile(prawnFileLocation, useSBM, userLinFits);
        ReportsEngine.produceReports(shrimpFractions);
    }

    private static PrawnFile unmarshallRawDataXML(String resource)
            throws MalformedURLException, JAXBException {
        PrawnFile myPrawnFile;

        jaxbContext = JAXBContext.newInstance(PrawnFile.class);
        jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        if (resource.toLowerCase(Locale.ENGLISH).startsWith("http")) {
            java.net.URL prawnDataURL;
            prawnDataURL = new URL(resource);
            myPrawnFile = readRawDataURL(prawnDataURL);
        } else {
            // assume file
            File prawnDataFile = new File(resource);
            myPrawnFile = readRawDataFile(prawnDataFile);
        }

        return myPrawnFile;

    }

    /**
     *
     * @param prawnDataFile the value of prawnDataFile
     * @return the PrawnFile
     * @throws javax.xml.bind.JAXBException
     */
    private static PrawnFile readRawDataFile(File prawnDataFile) throws JAXBException {

        PrawnFile myPrawnFile = (PrawnFile) jaxbUnmarshaller.unmarshal(prawnDataFile);
        return myPrawnFile;
    }

    /**
     *
     * @param prawnDataURL the value of prawnDataURL
     * @return the PrawnFile
     * @throws javax.xml.bind.JAXBException
     */
    private static PrawnFile readRawDataURL(URL prawnDataURL) throws JAXBException {

        PrawnFile myPrawnFile = (PrawnFile) jaxbUnmarshaller.unmarshal(prawnDataURL);
        return myPrawnFile;
    }

    /**
     * @return the currentPrawnFileLocation
     */
    public static String getCurrentPrawnFileLocation() {
        return currentPrawnFileLocation;
    }

    /**
     * @param aCurrentPrawnFileLocation the currentPrawnFileLocation to set
     */
    public static void setCurrentPrawnFileLocation(String aCurrentPrawnFileLocation) {
        currentPrawnFileLocation = aCurrentPrawnFileLocation;
    }

    public static void setProgressSubscriber(Consumer<Integer> progressSubscriber) {
        RawDataFileHandler.progressSubscriber = progressSubscriber;
    }

}
