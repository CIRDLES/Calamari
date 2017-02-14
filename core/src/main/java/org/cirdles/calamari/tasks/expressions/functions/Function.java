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
package org.cirdles.calamari.tasks.expressions.functions;

import com.thoughtworks.xstream.XStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.calamari.tasks.expressions.OperationOrFunctionInterface;
import org.cirdles.calamari.utilities.xmlSerialization.XMLSerializerInterface;

/**
 *
 * @author James F. Bowring
 */
public abstract class Function
        implements
        OperationOrFunctionInterface,
        XMLSerializerInterface {

    protected String name;
    protected int argumentCount;
    protected int precedence;

    @Override
    public void customizeXstream(XStream xstream) {
//        xstream.registerConverter(new OperationXMLConverter());
//        xstream.alias("operation", Function.class);
//        xstream.alias("operation", this.getClass());
    }

    public static OperationOrFunctionInterface ln() {
        return new Ln();
    }
    
    /**
     *
     * @param operationName
     * @return
     */
    public static OperationOrFunctionInterface operationFactory(String operationName) {
        Function retVal = null;
        Method method;
        if (operationName != null) {
            try {
                method = Function.class.getMethod(//
                        operationName,
                        new Class[0]);
                retVal = (Function) method.invoke(null, new Object[0]);
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
     * @return the argumentCount
     */
    public int getArgumentCount() {
        return argumentCount;
    }

    /**
     * @param argumentCount the argumentCount to set
     */
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
}
