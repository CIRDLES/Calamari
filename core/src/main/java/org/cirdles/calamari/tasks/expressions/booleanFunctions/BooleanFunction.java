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
package org.cirdles.calamari.tasks.expressions.booleanFunctions;

import org.cirdles.calamari.tasks.expressions.functions.*;
import com.thoughtworks.xstream.XStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.cirdles.calamari.tasks.expressions.BooleanOperationOrFunctionInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.calamari.tasks.expressions.OperationOrFunctionInterface;
import org.cirdles.calamari.utilities.xmlSerialization.XMLSerializerInterface;

/**
 *
 * @author James F. Bowring
 */
public abstract class BooleanFunction
        implements
        BooleanOperationOrFunctionInterface,
        XMLSerializerInterface {

    protected String name;
    protected int argumentCount;
    protected int precedence;
    // establish size of array resulting from evaluation
    protected int rowCount;
    protected int colCount;
    protected String[][] labelsForValues;

    @Override
    public void customizeXstream(XStream xstream) {
//        xstream.registerConverter(new OperationXMLConverter());
//        xstream.alias("operation", Function.class);
//        xstream.alias("operation", this.getClass());
    }

    public static BooleanOperationOrFunctionInterface and() {
        return null;
    }


    /**
     *
     * @param operationName
     * @return
     */
    public static BooleanOperationOrFunctionInterface operationFactory(String operationName) {
        BooleanFunction retVal = null;
        Method method;
        if (operationName != null) {
            try {
                method = BooleanFunction.class.getMethod(//
                        operationName,
                        new Class[0]);
                retVal = (BooleanFunction) method.invoke(null, new Object[0]);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException noSuchMethodException) {
                // do nothing for now
            }
        }
        return retVal;
    }

    protected String toStringAnotherExpression(ExpressionTreeInterface expression) {

        String retVal = "<mtext>\nNot a valid expression</mtext>\n";

        if (expression != null) {
            retVal = expression.toStringMathML();
        }

        return retVal;
    }

    /**
     * @return the name
     */
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
     * @return the argumentCount
     */
    @Override
    public int getArgumentCount() {
        return argumentCount;
    }

    /**
     * @param argumentCount the argumentCount to set
     */
    @Override
    public void setArgumentCount(int argumentCount) {
        this.argumentCount = argumentCount;
    }

    /**
     * @return the precedence
     */
    @Override
    public int getPrecedence() {
        return precedence;
    }

    /**
     * @return the rowCount
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * @param rowCount the rowCount to set
     */
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * @return the colCount
     */
    public int getColCount() {
        return colCount;
    }

    /**
     * @param colCount the colCount to set
     */
    public void setColCount(int colCount) {
        this.colCount = colCount;
    }

    /**
     * @return the labelsForValues
     */
    public String[][] getLabelsForValues() {
        return labelsForValues;
    }

    /**
     * @param labelsForValues the labelsForValues to set
     */
    public void setLabelsForValues(String[][] labelsForValues) {
        this.labelsForValues = labelsForValues;
    }
}
