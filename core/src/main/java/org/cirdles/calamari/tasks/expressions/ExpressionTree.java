/*
 * Copyright 2006-2017 CIRDLES.org.
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
package org.cirdles.calamari.tasks.expressions;

import com.thoughtworks.xstream.XStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.cirdles.calamari.shrimp.IsotopeNames;
import org.cirdles.calamari.shrimp.RawRatioNamesSHRIMP;
import org.cirdles.calamari.shrimp.RawRatioNamesSHRIMPXMLConverter;
import org.cirdles.calamari.tasks.expressions.operations.Add;
import org.cirdles.calamari.tasks.expressions.operations.Divide;
import org.cirdles.calamari.tasks.expressions.operations.Log;
import org.cirdles.calamari.tasks.expressions.operations.Multiply;
import org.cirdles.calamari.tasks.expressions.operations.Operation;
import org.cirdles.calamari.tasks.expressions.operations.OperationXMLConverter;
import org.cirdles.calamari.tasks.expressions.operations.Pow;
import org.cirdles.calamari.tasks.expressions.operations.Subtract;
import org.cirdles.calamari.utilities.xmlSerialization.XMLSerializerInterface;

/**
 *
 * @author James F. Bowring
 */
public class ExpressionTree implements ExpressionTreeInterface, ExpressionTreeWithRatiosInterface, XMLSerializerInterface {

    protected String name;
    protected ExpressionTreeInterface leftET;
    protected ExpressionTreeInterface rightET;
    protected Operation operation;
    protected List<RawRatioNamesSHRIMP> ratiosOfInterest;
    protected boolean rootExpressionTree;

    public ExpressionTree() {
        this("EMPTY");
    }

    /**
     *
     * @param prettyName the value of prettyName
     */
    public ExpressionTree(String prettyName) {
        this(prettyName, null, null, null);
    }

    /**
     *
     * @param prettyName the value of name
     * @param leftET the value of leftET
     * @param rightET the value of rightET
     * @param operation the value of operation
     */
    public ExpressionTree(String prettyName, ExpressionTreeInterface leftET, ExpressionTreeInterface rightET, Operation operation) {
        this(prettyName, leftET, rightET, operation, new ArrayList<RawRatioNamesSHRIMP>());
    }

    /**
     *
     * @param prettyName the value of prettyName
     * @param leftET the value of leftET
     * @param rightET the value of rightET
     * @param operation the value of operation
     * @param ratiosOfInterest the value of ratiosOfInterest
     */
    public ExpressionTree(String prettyName, ExpressionTreeInterface leftET, ExpressionTreeInterface rightET, Operation operation, List<RawRatioNamesSHRIMP> ratiosOfInterest) {
        this.name = prettyName;
        this.leftET = leftET;
        this.rightET = rightET;
        this.operation = operation;
        this.ratiosOfInterest = ratiosOfInterest;
        this.rootExpressionTree = false;
    }

    @Override
    public void customizeXstream(XStream xstream) {
        xstream.registerConverter(new ShrimpSpeciesNodeXMLConverter());
        xstream.alias("ShrimpSpeciesNode", ShrimpSpeciesNode.class);

        xstream.registerConverter(new ConstantNodeXMLConverter());
        xstream.alias("ConstantNode", ConstantNode.class);

        xstream.registerConverter(new OperationXMLConverter());
        xstream.alias("operation", Operation.class);
        xstream.alias("operation", Add.class);
        xstream.alias("operation", Subtract.class);
        xstream.alias("operation", Multiply.class);
        xstream.alias("operation", Divide.class);
        xstream.alias("operation", Pow.class);
        xstream.alias("operation", Log.class);

        xstream.registerConverter(new RawRatioNamesSHRIMPXMLConverter());
        xstream.alias("ratio", RawRatioNamesSHRIMP.class);

        xstream.registerConverter(new ExpressionTreeXMLConverter());
        xstream.alias("ExpressionTree", ExpressionTree.class);
    }

    /**
     *
     * @param xml
     * @return
     */
    @Override
    public String customizeXML(String xml) {
        String xmlR = xml;

        // TODO: Move to global once we decide where this puppy will live
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<ExpressionTree xmlns=\"https://raw.githubusercontent.com\"\n"
                + " xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n"
                + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + " xsi:schemaLocation=\"https://raw.githubusercontent.com\n"
                + "                 https://raw.githubusercontent.com/bowring/Calamari/expressions/src/main/resources/SquidExpressionModelXMLSchema.xsd\">";

        xmlR = xmlR.replaceFirst("<ExpressionTree>",
                header);

        return xmlR;
    }

    /**
     *
     * @param pkInterpScan the value of pkInterpScan
     * @param isotopeToIndexMap the value of isotopeToIndexMap
     * @return the double
     */
    @Override
    public double eval(double[] pkInterpScan, Map<IsotopeNames, Integer> isotopeToIndexMap) {
        return operation == null ? 0.0 : operation.eval(leftET, rightET, pkInterpScan, isotopeToIndexMap);
    }

    @Override
    public Set extractUniqueSpeciesNumbers() {
        // assume acquisition order is atomic weight order
        Set<IsotopeNames> eqPkUndupeOrd = new TreeSet<>();
        for (int i = 0; i < ratiosOfInterest.size(); i++) {
            eqPkUndupeOrd.add(ratiosOfInterest.get(i).getNumerator());
            eqPkUndupeOrd.add(ratiosOfInterest.get(i).getDenominator());
        }
        return eqPkUndupeOrd;
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the leftET
     */
    public ExpressionTreeInterface getLeftET() {
        return leftET;
    }

    /**
     * @param leftET the leftET to set
     */
    public void setLeftET(ExpressionTreeInterface leftET) {
        this.leftET = leftET;
    }

    /**
     * @return the rightET
     */
    public ExpressionTreeInterface getRightET() {
        return rightET;
    }

    /**
     * @param rightET the rightET to set
     */
    public void setRightET(ExpressionTreeInterface rightET) {
        this.rightET = rightET;
    }

    /**
     * @return the operation
     */
    public Operation getOperation() {
        return operation;
    }

    /**
     * @param operation the operation to set
     */
    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    /**
     * @return the ratiosOfInterest
     */
    @Override
    public List<RawRatioNamesSHRIMP> getRatiosOfInterest() {
        return ratiosOfInterest;
    }

    /**
     * @param ratiosOfInterest the ratiosOfInterest to set
     */
    public void setRatiosOfInterest(List<RawRatioNamesSHRIMP> ratiosOfInterest) {
        this.ratiosOfInterest = ratiosOfInterest;
    }

    /**
     * @return the rootExpressionTree
     */
    public boolean isRootExpressionTree() {
        return rootExpressionTree;
    }

    /**
     * @param rootExpressionTree the rootExpressionTree to set
     */
    public void setRootExpressionTree(boolean rootExpressionTree) {
        this.rootExpressionTree = rootExpressionTree;
    }

}
