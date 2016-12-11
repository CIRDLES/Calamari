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
package org.cirdles.calamari.utilities.xmlSerialization;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public interface XMLSerializerInterface {

    /**
     * Use XStream to serialize object to XML
     *
     * @param object
     * @param filename
     */
    public default void serializeXMLObject(Object object, String filename) {
        FileWriter outFile = null;
        try {
            XStream xstream = new XStream(new DomDriver());
            customizeXstream(xstream);
            String xml = xstream.toXML(object).trim();
            xml = customizeXML(xml).trim();

            // TODO: Move to global once we decide where this puppy will live
            String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<ExpressionTree xmlns=\"https://raw.githubusercontent.com\"\n"
                    + " xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n"
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                    + " xsi:schemaLocation=\"https://raw.githubusercontent.com\n"
                    + "                 https://raw.githubusercontent.com/bowring/Calamari/expressions/src/main/resources/SquidExpressionModelXMLSchema.xsd\">";

            xml = xml.replaceFirst("<ExpressionTree>",
                    header);

            outFile = new FileWriter(filename);
            try (PrintWriter out = new PrintWriter(outFile)) {
                // Write xml to file
                out.println(xml);
                out.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(XMLSerializerInterface.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (outFile != null) {
                    outFile.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(XMLSerializerInterface.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public default ExpressionTreeInterface readXMLObject(String filename, boolean doValidate) {

        String xmlContents = null;
        ExpressionTreeInterface myModelClassInstance = null;

        try {
            xmlContents = new String(Files.readAllBytes(Paths.get(filename)));
            XStream xstream = new XStream(new DomDriver());
            customizeXstream(xstream);
            myModelClassInstance = (ExpressionTreeInterface) xstream.fromXML(xmlContents);
        } catch (IOException iOException) {
            // do nothing for now
        }

        return myModelClassInstance;
    }

    public void customizeXstream(XStream xsteam);

    public default String customizeXML(String xml) {
        return xml;
    }
}
