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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.cirdles.calamari.prawn.PrawnFile;
import org.cirdles.calamari.prawn.PrawnRunFractionParser;
import org.cirdles.calamari.shrimp.ShrimpFraction;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class RawDataFileHandler {

    private static JAXBContext jaxbContext;
    private static Unmarshaller jaxbUnmarshaller;

    public static List<ShrimpFraction> extractShrimpFractionsFromPrawnFile(String prawnFileLocation)
            throws MalformedURLException, JAXBException {
        PrawnFile prawnFile = unmarshallRawDataXML(prawnFileLocation);
        String nameOfMount = prawnFile.getMount();
        List<ShrimpFraction> shrimpFractions = new ArrayList<>();

        for (int f = 0; f < prawnFile.getRuns(); f++) {
            PrawnFile.Run runFraction = prawnFile.getRun().get(f);
            ShrimpFraction shrimpFraction = PrawnRunFractionParser.processRunFraction(runFraction);
            shrimpFraction.setSpotNumber(f + 1);
            shrimpFraction.setNameOfMount(nameOfMount);
            shrimpFractions.add(shrimpFraction);
        }

        return shrimpFractions;
    }

    public static void writeReportsFromPrawnFile(String prawnFileLocation)
            throws IOException, MalformedURLException, JAXBException {
        List<ShrimpFraction> shrimpFractions = extractShrimpFractionsFromPrawnFile(prawnFileLocation);
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

}
