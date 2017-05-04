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
import org.cirdles.calamari.tasks.expressions.isotopes.ShrimpSpeciesNode;

/**
 *
 * @author James F. Bowring
 */
public class Divide extends Operation {

    public Divide() {
        name = "divide";
        argumentCount = 2;
        precedence = 3;
    }

    /**
     *
     * @param childrenET the value of childrenET
     * @param shrimpFractions the value of shrimpFraction
     * @return the double[][]
     */
    @Override
    public Object[][] eval2Array(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions) {

        double retVal;
        try {
            retVal = (double)childrenET.get(0).eval(shrimpFractions)[0][0]
                    / (double)childrenET.get(1).eval(shrimpFractions)[0][0];
        } catch (Exception e) {
            retVal = 0.0;
        }

        // April 2017 constrain quotient to mimic VBA results for isotopic ratios
        // by providing only 12 significant digits per Simon Bodorkos
        if (childrenET.get(0) instanceof ShrimpSpeciesNode) {
            retVal = org.cirdles.ludwig.squid25.Utilities.roundedToSize(retVal, 12);
        }

        return new Object[][]{{retVal}};
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
                = "<mfrac>\n"
                + "<mrow>\n"
                + (leftChildHasLowerPrecedence ? "<mo>(</mo>\n" : "")
                + toStringAnotherExpression(childrenET.get(0))
                + (leftChildHasLowerPrecedence ? "<mo>)</mo>\n" : "")
                + "\n</mrow>\n"
                + "<mrow>\n"
                + (rightChildHasLowerPrecedence ? "<mo>(</mo>\n" : "")
                + toStringAnotherExpression(childrenET.get(1))
                + (rightChildHasLowerPrecedence ? "<mo>)</mo>\n" : "")
                + "\n</mrow>\n"
                + "</mfrac>\n";

        return retVal;
    }

}
