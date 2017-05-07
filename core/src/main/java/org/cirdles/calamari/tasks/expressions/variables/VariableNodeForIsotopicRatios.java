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
import org.cirdles.calamari.tasks.TaskInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.calamari.utilities.xmlSerialization.XMLSerializerInterface;
import static org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface.convertArrayToObjects;

/**
 *
 * @author James F. Bowring
 */
public class VariableNodeForIsotopicRatios implements ExpressionTreeInterface, XMLSerializerInterface {

    private String name;
    private static String lookupMethodNameForShrimpFraction = "getIsotopicRatioValuesByStringName";
    private ExpressionTreeInterface parentET;

    public VariableNodeForIsotopicRatios() {
        this(null);
    }

    public VariableNodeForIsotopicRatios(String name) {
        this.name = name;
    }

    @Override
    public void customizeXstream(XStream xstream) {
//        xstream.registerConverter(new ShrimpSpeciesNodeXMLConverter());
//        xstream.alias("ShrimpSpeciesNode", VariableNode.class);
    }

    /**
     * Returns an array of values from a column (name) of spots
     * (shrimpFractions) by using the specified lookup Method of
     * ShrimpFractionExpressionInterface
     *
     * @param shrimpFractions
     * @param task
     * @return
     */
    @Override
    public Object[][] eval(List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) {
        Object[][] retVal = new Object[shrimpFractions.size()][];

        try {
            Method method = ShrimpFractionExpressionInterface.class.getMethod(//
                    lookupMethodNameForShrimpFraction,
                    new Class[]{String.class});
            for (int i = 0; i < shrimpFractions.size(); i++) {
                double[] values = ((double[][]) method.invoke(shrimpFractions.get(i), new Object[]{name}))[0];
                retVal[i] = convertArrayToObjects(values);
            }

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException methodException) {
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
