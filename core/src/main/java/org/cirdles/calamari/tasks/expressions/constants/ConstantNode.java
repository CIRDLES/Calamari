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
package org.cirdles.calamari.tasks.expressions.constants;

import com.thoughtworks.xstream.XStream;
import java.util.List;
import java.util.Map;
import org.cirdles.calamari.shrimp.IsotopeNames;
import org.cirdles.calamari.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.calamari.utilities.xmlSerialization.XMLSerializerInterface;

/**
 *
 * @author James F. Bowring
 */
public class ConstantNode implements ExpressionTreeInterface, XMLSerializerInterface {

    private String name;
    private double value;
    private ExpressionTreeInterface parentET;

    public ConstantNode() {
        this("", 0.0);
    }

    public ConstantNode(String name, double value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void customizeXstream(XStream xstream) {
        xstream.registerConverter(new ConstantNodeXMLConverter());
        xstream.alias("ConstantNode", ConstantNode.class);
    }

    /**
     *
     * @param pkInterpScan the value of pkInterpScan
     * @param isotopeToIndexMap the value of isotopeToIndexMap
     * @return the double
     */
    @Override
    public double eval(double[] pkInterpScan, Map<IsotopeNames, Integer> isotopeToIndexMap) {
        return value;
    }

    /**
     *
     * @param shrimpFractions the value of shrimpFraction
     * @param pkInterpScan the value of pkInterpScan
     * @param isotopeToIndexMap the value of isotopeToIndexMap
     * @return the double[][]
     */
    @Override
    public double[][] eval2Array(List<ShrimpFractionExpressionInterface> shrimpFractions, double[] pkInterpScan, Map<IsotopeNames, Integer> isotopeToIndexMap) {
        return new double[][]{{value}};
    }

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
     * @return the value
     */
    public double getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toStringMathML() {
        return "<mn>" + name + "</mn>\n";
    }

    @Override
    public boolean isRootExpressionTree() {
        return false;
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
     *
     * @return
     */
    @Override
    public boolean isTypeFunction() {
        return false;
    }

    @Override
    public boolean isTypeFunctionOrOperation() {
       return false;
    }

    @Override
    public int argumentCount() {
        return 0;
    }
}
