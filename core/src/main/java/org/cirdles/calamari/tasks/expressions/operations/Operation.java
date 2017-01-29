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
package org.cirdles.calamari.tasks.expressions.operations;

import com.thoughtworks.xstream.XStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import org.cirdles.calamari.shrimp.IsotopeNames;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.calamari.tasks.expressions.OperationOrFunctionInterface;
import org.cirdles.calamari.utilities.xmlSerialization.XMLSerializerInterface;

/**
 *
 * @author James F. Bowring
 */
public abstract class Operation
        implements
        OperationOrFunctionInterface,
        XMLSerializerInterface {

    protected String name;
    protected int precedence;

    public abstract double eval(ExpressionTreeInterface leftET, ExpressionTreeInterface rightET, double[] pkInterpScan, Map<IsotopeNames, Integer> isotopeToIndexMap);

    @Override
    public double eval(List<ExpressionTreeInterface> childrenET, double[] pkInterpScan, Map<IsotopeNames, Integer> isotopeToIndexMap) {
        return eval(childrenET.get(0), childrenET.get(1), pkInterpScan, isotopeToIndexMap);
    }

    @Override
    public void customizeXstream(XStream xstream) {
        xstream.registerConverter(new OperationXMLConverter());
        xstream.alias("operation", Operation.class);
        xstream.alias("operation", this.getClass());
    }

    public static OperationOrFunctionInterface add() {
        return new Add();
    }

    public static OperationOrFunctionInterface subtract() {
        return new Subtract();
    }

    public static OperationOrFunctionInterface divide() {
        return new Divide();
    }

    public static OperationOrFunctionInterface multiply() {
        return new Multiply();
    }

    public static OperationOrFunctionInterface pow() {
        return new Pow();
    }

    /**
     *
     * @return
     */
    public static OperationOrFunctionInterface log() {
        return new Log();
    }

    /**
     *
     * @return
     */
    public static OperationOrFunctionInterface pExp() {
        return new Pexp();
    }

    /**
     *
     * @param operationName
     * @return
     */
    public static OperationOrFunctionInterface operationFactory(String operationName) {
        Operation retVal = null;
        Method method;
        if (operationName != null) {
            try {
                method = Operation.class.getMethod(//
                        operationName,
                        new Class[0]);
                retVal = (Operation) method.invoke(null, new Object[0]);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException noSuchMethodException) {
                // do nothing for now
            }
        }
        return retVal;
    }

    protected String toStringAnotherExpression(ExpressionTreeInterface expression) {
        String retVal = expression.toStringMathML();
        if (expression.isRootExpressionTree()) {
            retVal
                    = "<mtext>\n"
                    + "[Expression "
                    + expression.getName()
                    + "]\n"
                    + "</mtext>\n";
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
     * @return the precedence
     */
    @Override
    public int getPrecedence() {
        return precedence;
    }
}
