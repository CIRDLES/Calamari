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
import org.cirdles.calamari.tasks.expressions.constants.ConstantNode;
import org.cirdles.calamari.tasks.expressions.constants.ConstantNodeXMLConverter;
import org.cirdles.calamari.tasks.expressions.functions.Function;
import org.cirdles.calamari.tasks.expressions.functions.Ln;
import org.cirdles.calamari.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.calamari.tasks.expressions.isotopes.ShrimpSpeciesNodeXMLConverter;
import org.cirdles.calamari.tasks.expressions.operations.Add;
import org.cirdles.calamari.tasks.expressions.operations.Divide;
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
public class ExpressionTree
        implements ExpressionTreeInterface,
        ExpressionTreeBuilderInterface,
        ExpressionTreeWithRatiosInterface,
        XMLSerializerInterface {

    protected String name;
    private ExpressionTreeInterface parentET;
    protected ExpressionTreeInterface leftET;
    protected ExpressionTreeInterface rightET;
    protected List<ExpressionTreeInterface> childrenET;
    protected OperationOrFunctionInterface operation;
    protected List<RawRatioNamesSHRIMP> ratiosOfInterest;
    protected boolean rootExpressionTree;

    public ExpressionTree() {
        this("No Name");
    }

    /**
     *
     * @param prettyName the value of prettyName
     */
    public ExpressionTree(String prettyName) {
        this(prettyName, null, null, null);
    }

    public ExpressionTree(OperationOrFunctionInterface operation) {
        this();
        this.operation = operation;
    }

    /**
     *
     * @param prettyName the value of name
     * @param leftET the value of leftET
     * @param rightET the value of rightET
     * @param operation the value of operation
     */
    public ExpressionTree(String prettyName, ExpressionTreeInterface leftET, ExpressionTreeInterface rightET, OperationOrFunctionInterface operation) {
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
    public ExpressionTree(String prettyName, ExpressionTreeInterface leftET, ExpressionTreeInterface rightET, OperationOrFunctionInterface operation, List<RawRatioNamesSHRIMP> ratiosOfInterest) {
        this.name = prettyName;
        this.leftET = leftET;
        this.rightET = rightET;
        this.operation = operation;
        this.ratiosOfInterest = ratiosOfInterest;
        this.rootExpressionTree = false;
        this.childrenET = new ArrayList<>();
        addChild(leftET);
        addChild(rightET);
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

        xstream.alias("function", Ln.class);

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
        return operation == null ? 0.0 : operation.eval(childrenET, pkInterpScan, isotopeToIndexMap);
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

    @Override
    public int getOperationPrecedence() {
        int retVal = 100;

        if (operation != null) {
            retVal = operation.getPrecedence();
        }

        return retVal;
    }

    @Override
    public boolean isTypeFunction() {
        return (operation instanceof Function);
    }
    
    @Override
    public int argumentCount(){
        int retVal = -1;
        if (isTypeFunction()){
            retVal = ((Function)operation).getArgumentCount();
        }
        
        return retVal;
    }
    
    @Override
    public String toStringMathML() {
        String retVal = "";
        if (operation == null) {
            retVal = "<mtext>No expression selected.</mtext>\n";
        } else {
            retVal = operation.toStringMathML(leftET, rightET, childrenET);
        }
        return retVal;
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
     * @return the parentET
     */
    @Override
    public ExpressionTreeInterface getParentET() {
        return parentET;
    }

    /**
     * @param parentET the parentET to set
     */
    @Override
    public void setParentET(ExpressionTreeInterface parentET) {
        this.parentET = parentET;
    }

    /**
     * @return the leftET
     */
    @Override
    public ExpressionTreeInterface getLeftET() {
        ExpressionTreeInterface retVal = null;
        try {
            retVal = childrenET.get(0);
        } catch (Exception e) {
        }
        return retVal;
    }

    /**
     * @param leftET the leftET to set
     */
    @Override
    public void setLeftET(ExpressionTreeInterface leftET) {
        this.leftET = leftET;
        try {
            if (childrenET.get(0) == null) {
                childrenET.remove(0);
            }
        } catch (Exception e) {
        }
        addChild(0, leftET);
    }

    /**
     * @return the rightET
     */
    @Override
    public ExpressionTreeInterface getRightET() {
        ExpressionTreeInterface retVal = null;
        try {
            retVal = childrenET.get(1);
        } catch (Exception e) {
        }
        return retVal;
    }

    /**
     * @param rightET the rightET to set
     */
    @Override
    public void setRightET(ExpressionTreeInterface rightET) {
        this.rightET = rightET;
        if (childrenET.isEmpty()) {
            // add in null left for logic
            childrenET.add(null);
        }

        addChild(rightET);
    }
    
    @Override
    public int getCountOfChildren(){
        return childrenET.size();
    }

    /**
     *
     * @param childET
     */
    public void addChild(ExpressionTreeInterface childET) {
        if (childET != null) {
            childrenET.add(childET);
            childET.setParentET(this);
        }
    }

    public void addChild(int index, ExpressionTreeInterface childET) {
        if (childET != null) {
            childrenET.add(index, childET);
            childET.setParentET(this);
        }
    }

    /**
     * @return the operation
     */
    @Override
    public OperationOrFunctionInterface getOperation() {
        return operation;
    }

    /**
     * @param operation the operation to set
     */
    @Override
    public void setOperation(OperationOrFunctionInterface operation) {
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
    @Override
    public boolean isRootExpressionTree() {
        return rootExpressionTree;
    }

    /**
     * @param rootExpressionTree the rootExpressionTree to set
     */
    public void setRootExpressionTree(boolean rootExpressionTree) {
        this.rootExpressionTree = rootExpressionTree;
    }

    @Override
    public String toString() {
        return name;
    }
}
