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

import java.util.List;
import org.cirdles.calamari.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeBuilderInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring
 */
public class Multiply extends Operation {

    public Multiply() {
        name = "multiply";
        argumentCount = 2;
        precedence = 3;
        rowCount = 1;
        colCount = 1;
    }

    /**
     *
     * @param childrenET the value of childrenET
     * @param shrimpFractions the value of shrimpFraction
     * @return the double[][]
     */
    @Override
    public double[][] eval2Array(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions) {

        double retVal;
        try {
            retVal = childrenET.get(0).eval2Array(shrimpFractions)[0][0]
                    * childrenET.get(1).eval2Array(shrimpFractions)[0][0];
        } catch (Exception e) {
            retVal = 0.0;
        }
        return new double[][]{{retVal}};
    }

    /**
     *
     * @param childrenET the value of childrenET
     * @return
     */
    @Override
    public String toStringMathML(List<ExpressionTreeInterface> childrenET) {
        boolean leftChildHasLowerPrecedence = false;
        try {
            leftChildHasLowerPrecedence = precedence > ((ExpressionTreeBuilderInterface) childrenET.get(0)).getOperationPrecedence();
        } catch (Exception e) {
        }
        boolean rightChildHasLowerPrecedence = false;
        try {
            rightChildHasLowerPrecedence = precedence > ((ExpressionTreeBuilderInterface) childrenET.get(1)).getOperationPrecedence();
        } catch (Exception e) {
        }

        String retVal
                = "<mrow>\n"
                + (leftChildHasLowerPrecedence ? "<mo>(</mo>\n" : "")
                + toStringAnotherExpression(childrenET.get(0))
                + (leftChildHasLowerPrecedence ? "<mo>)</mo>\n" : "")
                + "<mo>&times;</mo>\n"
                + (rightChildHasLowerPrecedence ? "<mo>(</mo>\n" : "")
                + toStringAnotherExpression(childrenET.get(1))
                + (rightChildHasLowerPrecedence ? "<mo>)</mo>\n" : "")
                + "</mrow>\n";

        return retVal;
    }

}
