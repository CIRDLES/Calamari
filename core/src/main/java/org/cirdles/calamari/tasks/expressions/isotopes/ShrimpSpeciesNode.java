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
package org.cirdles.calamari.tasks.expressions.isotopes;

import com.thoughtworks.xstream.XStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
public class ShrimpSpeciesNode implements ExpressionTreeInterface, XMLSerializerInterface {

    private IsotopeNames name;
    private String methodNameForShrimpFraction;
    private ExpressionTreeInterface parentET;

    public ShrimpSpeciesNode() {
        this(null);
    }

    public ShrimpSpeciesNode(IsotopeNames name) {
        this(name, null);
    }

    public ShrimpSpeciesNode(IsotopeNames name, String methodNameForShrimpFraction) {
        this.name = name;
        this.methodNameForShrimpFraction = methodNameForShrimpFraction;
    }

    @Override
    public void customizeXstream(XStream xstream) {
        xstream.registerConverter(new ShrimpSpeciesNodeXMLConverter());
        xstream.alias("ShrimpSpeciesNode", ShrimpSpeciesNode.class);
    }

    /**
     *
     * @param pkInterpScan the value of pkInterpScan
     * @param isotopeToIndexMap the value of isotopeToIndexMap
     * @return the double
     */
    @Override
    public double eval(double[] pkInterpScan, Map<IsotopeNames, Integer> isotopeToIndexMap) {
        double retVal = 0.0;
        Integer index = isotopeToIndexMap.get(name);
        if (index != null) {
            retVal = pkInterpScan[index];
        }
        return retVal;
    }

    /**
     *
     * @param shrimpFractions the value of shrimpFraction
     * @param pkInterpScan the value of pkInterpScan
     * @param isotopeToIndexMap the value of isotopeToIndexMap
     * @return the double[][]
     */
    @Override
    public double[][] eval2Array(List<ShrimpFractionExpressionInterface>  shrimpFractions, double[] pkInterpScan, Map<IsotopeNames, Integer> isotopeToIndexMap) {
        double retVal = 0.0;
        Integer index = isotopeToIndexMap.get(name);
        if (index != null) {
            double[] isotopeValues = 
                    methodFactory(shrimpFractions.get(0), methodNameForShrimpFraction);
            if (index < isotopeValues.length) {
                retVal = isotopeValues[index];
            }
        }

        return new double[][]{{retVal}};
    }

    /**
     *
     * @param shrimpFraction
     * @param methodNameForShrimpFraction
     * @return
     */
    public static double[] methodFactory(ShrimpFractionExpressionInterface shrimpFraction, String methodNameForShrimpFraction) {
        double[] retVal = new double[0];
        Method method;
        if (methodNameForShrimpFraction != null) {
            try {
                method = ShrimpFractionExpressionInterface.class.getMethod(//
                        methodNameForShrimpFraction,
                        new Class[0]);
                retVal = (double[]) method.invoke(shrimpFraction, new Object[0]);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException noSuchMethodException) {
                // do nothing for now
            }
        }
        return retVal;
    }

    @Override
    public String toStringMathML() {
        String retVal
                = "<msubsup>\n"
                + "<mstyle mathsize='90%'>\n"
                + "<mtext>\n"
                + name.getAtomicMass()
                + "\n</mtext>\n"
                + "</mstyle>\n"
                + "<mstyle  mathsize='150%'>\n"
                + "<mtext>\n"
                + name.getElementName()
                + "\n</mtext>\n"
                + "</mstyle>\n"
                + "</msubsup>\n";

        return retVal;
    }

    @Override
    public String getName() {
        return name.getName();
    }

    /**
     * @param name the name to set
     */
    public void setName(IsotopeNames name) {
        this.name = name;
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
