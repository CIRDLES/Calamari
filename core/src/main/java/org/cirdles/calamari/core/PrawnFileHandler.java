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
import org.cirdles.calamari.prawn.PrawnFileRunFractionParser;
import org.cirdles.calamari.shrimp.ShrimpFraction;

/**
 * Handles common operations involving Prawn files.
 */
public class PrawnFileHandler {

    private Unmarshaller jaxbUnmarshaller;
    private String currentPrawnFileLocation;
    private Consumer<Integer> progressSubscriber;
    private CalamariReportsEngine reportsEngine;

    private static final PrawnFileRunFractionParser PRAWN_FILE_RUN_FRACTION_PARSER
            = new PrawnFileRunFractionParser();

    /**
     * Creates a new {@link PrawnFileHandler} using a new reports engine.
     */
    public PrawnFileHandler() {
        this(new CalamariReportsEngine());
    }

    /**
     * Creates a new {@link PrawnFileHandler}.
     *
     * @param reportsEngine the reports engine to use
     */
    public PrawnFileHandler(CalamariReportsEngine reportsEngine) {
//        currentPrawnFileLocation = "https://raw.githubusercontent.com/bowring/XSD/master/SHRIMP/EXAMPLE_100142_G6147_10111109.43_10.33.37%20AM.xml";
        this.reportsEngine = reportsEngine;
    }

    /**
     * @param prawnFileLocation the value of prawnFileLocation
     * @param useSBM the value of useSBM
     * @param userLinFits the value of userLinFits
     * @return
     * @throws MalformedURLException
     * @throws JAXBException
     */
    public List<ShrimpFraction> extractShrimpFractionsFromPrawnFile(String prawnFileLocation, boolean useSBM, boolean userLinFits)
            throws MalformedURLException, JAXBException {
        currentPrawnFileLocation = prawnFileLocation;

        PrawnFile prawnFile = unmarshallRawDataXML(prawnFileLocation);

        String nameOfMount = prawnFile.getMount();
        if (nameOfMount == null) {
            nameOfMount = "No-Mount-Name";
        }
        List<ShrimpFraction> shrimpFractions = new ArrayList<>();

        // July 2016 prawnFile.getRuns() is not reliable
        for (int f = 0; f < prawnFile.getRun().size(); f++) {
            PrawnFile.Run runFraction = prawnFile.getRun().get(f);
//            if (runFraction.getPar().get(0).getValue().compareToIgnoreCase("ILB-23.1") == 0) {
//                System.out.println("SHRIMPFRACTION " + runFraction.getPar().get(0).getValue());
            ShrimpFraction shrimpFraction = PRAWN_FILE_RUN_FRACTION_PARSER.processRunFraction(runFraction, useSBM, userLinFits);
            if (shrimpFraction != null) {
                shrimpFraction.setSpotNumber(f + 1);
                shrimpFraction.setNameOfMount(nameOfMount);
                shrimpFractions.add(shrimpFraction);
            }
//            }

            if (progressSubscriber != null) {
                int progress = (f + 1) * 100 / prawnFile.getRun().size();
                progressSubscriber.accept(progress);
            }
        }

        return shrimpFractions;
    }

    /**
     * @param prawnFileLocation the value of prawnFileLocation
     * @param useSBM the value of useSBM
     * @param userLinFits the value of userLinFits
     * @throws IOException
     * @throws MalformedURLException
     * @throws JAXBException
     */
    public void writeReportsFromPrawnFile(String prawnFileLocation, boolean useSBM, boolean userLinFits)
            throws IOException, MalformedURLException, JAXBException {
        List<ShrimpFraction> shrimpFractions = extractShrimpFractionsFromPrawnFile(prawnFileLocation, useSBM, userLinFits);
        reportsEngine.produceReports(shrimpFractions);
    }

    private PrawnFile unmarshallRawDataXML(String resource)
            throws MalformedURLException, JAXBException {
        PrawnFile myPrawnFile;

        JAXBContext jaxbContext = JAXBContext.newInstance(PrawnFile.class);
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
     * @param prawnDataFile the value of prawnDataFile
     * @return the PrawnFile
     * @throws javax.xml.bind.JAXBException
     */
    private PrawnFile readRawDataFile(File prawnDataFile) throws JAXBException {

        PrawnFile myPrawnFile = (PrawnFile) jaxbUnmarshaller.unmarshal(prawnDataFile);
        return myPrawnFile;
    }

    /**
     * @param prawnDataURL the value of prawnDataURL
     * @return the PrawnFile
     * @throws javax.xml.bind.JAXBException
     */
    private PrawnFile readRawDataURL(URL prawnDataURL) throws JAXBException {

        PrawnFile myPrawnFile = (PrawnFile) jaxbUnmarshaller.unmarshal(prawnDataURL);
        return myPrawnFile;
    }

    public boolean currentPrawnFileLocationIsFile() {
        return new File(currentPrawnFileLocation).isFile();
    }

    public void initReportsEngineWithCurrentPrawnFileName() {
        // strip .xml from file name
        reportsEngine.setNameOfPrawnXMLFile(new File(currentPrawnFileLocation).getName().split("\\.")[0]);
    }

    public void initReportsEngineWithCurrentPrawnFileName(String prawnFileLocation) {
        // strip .xml from file name
        reportsEngine.setNameOfPrawnXMLFile(new File(prawnFileLocation).getName().split("\\.")[0]);
    }

    /**
     * @return the currentPrawnFileLocation
     */
    public String getCurrentPrawnFileLocation() {
        return currentPrawnFileLocation;
    }

    /**
     * @param aCurrentPrawnFileLocation the currentPrawnFileLocation to set
     */
    public void setCurrentPrawnFileLocation(String aCurrentPrawnFileLocation) {
        currentPrawnFileLocation = aCurrentPrawnFileLocation;
    }

    public void setProgressSubscriber(Consumer<Integer> progressSubscriber) {
        this.progressSubscriber = progressSubscriber;
    }

    public CalamariReportsEngine getReportsEngine() {
        return reportsEngine;
    }

}
