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
package org.cirdles.calamari.tasks.expressions.variables;

import com.thoughtworks.xstream.XStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.cirdles.calamari.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.calamari.tasks.Task;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.calamari.utilities.xmlSerialization.XMLSerializerInterface;

/**
 *
 * @author James F. Bowring
 */
public class VariableNode implements ExpressionTreeInterface, XMLSerializerInterface {

    private String name;
    private String lookupMethodNameForShrimpFraction;
    private ExpressionTreeInterface parentET;

    public VariableNode() {
        this(null);
    }

    public VariableNode(String name) {
        this(name, null);
    }

    public VariableNode(String name, String methodNameForShrimpFraction) {
        this.name = name;
        this.lookupMethodNameForShrimpFraction = methodNameForShrimpFraction;
    }

    @Override
    public void customizeXstream(XStream xstream) {
//        xstream.registerConverter(new ShrimpSpeciesNodeXMLConverter());
//        xstream.alias("ShrimpSpeciesNode", VariableNode.class);
    }

    /**
     * Returns an array of values from a column (name) of spots
     * (shrimpFractions) by using the specified lookup Method such as 
     * getTaskExpressionsEvaluationsPerSpotByField that takes
     * a field name as argument = name of variable.
     *
     * @param shrimpFractions
     * @return
     */
    @Override
    public Object[][] eval2Array(List<ShrimpFractionExpressionInterface> shrimpFractions) {
        Object [][] retVal = new Object[shrimpFractions.size()][];

        if (lookupMethodNameForShrimpFraction != null) {
            try {
                Method method = ShrimpFractionExpressionInterface.class.getMethod(//
                        lookupMethodNameForShrimpFraction,
                        new Class[]{String.class});
                for (int i = 0; i < shrimpFractions.size(); i ++){
                    double [] values = ((double[][]) method.invoke(shrimpFractions.get(i), new Object[]{name}))[0];
                    retVal[i] = Task.convertDoubleArray(values);
                }
                
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException methodException) {
            }
        }
   
        return retVal;
    }

    @Override
    public String toStringMathML() {
        String retVal
                = "<mtext>\n"
                + name
                + "</mtext>\n";

        return retVal;
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
