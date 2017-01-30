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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import org.cirdles.calamari.shrimp.IsotopeNames;
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
        precedence = 3;
    }

    /**
     *
     * @param leftET the value of leftET
     * @param rightET the value of rightET
     * @param pkInterpScan the value of pkInterpScan
     * @param isotopeToIndexMap the value of isotopeToIndexMap
     * @return the double
     */
    @Override
    public double eval(
            ExpressionTreeInterface leftET,
            ExpressionTreeInterface rightET,
            double[] pkInterpScan,
            Map<IsotopeNames, Integer> isotopeToIndexMap) {
        double retVal;

        try {
            retVal = leftET.eval(pkInterpScan, isotopeToIndexMap) / rightET.eval(pkInterpScan, isotopeToIndexMap);
        } catch (Exception e) {
            retVal = 0.0;
        }

        // Experiment to mimic VBA results
        if (leftET instanceof ShrimpSpeciesNode) {
            BigDecimal ratio = new BigDecimal(retVal);
            int newScale = 15 - (ratio.precision() - ratio.scale());
            BigDecimal ratio2 = ratio.setScale(newScale, RoundingMode.HALF_EVEN);
            retVal = ratio2.doubleValue();
        }

        return retVal;
    }

    /**
     *
     * @param leftET the value of leftET
     * @param rightET the value of rightET
     * @param childrenET the value of childrenET
     */
    @Override
    public String toStringMathML(ExpressionTreeInterface leftET, ExpressionTreeInterface rightET, List<ExpressionTreeInterface> childrenET) {
        boolean leftChildHasLowerPrecedence = false;
        try {
            leftChildHasLowerPrecedence = precedence > ((ExpressionTreeBuilderInterface) leftET).getOperationPrecedence();
        } catch (Exception e) {
        }
        boolean rightChildHasLowerPrecedence = false;
        try {
            rightChildHasLowerPrecedence = precedence > ((ExpressionTreeBuilderInterface) rightET).getOperationPrecedence();
        } catch (Exception e) {
        }

        String retVal
                = "<mfrac>\n"
                + "<mrow>\n"
                + (leftChildHasLowerPrecedence ? "<mo>(</mo>\n" : "")
                + toStringAnotherExpression(leftET)
                + (leftChildHasLowerPrecedence ? "<mo>)</mo>\n" : "")
                + "\n</mrow>\n"
                + "<mrow>\n"
                + (rightChildHasLowerPrecedence ? "<mo>(</mo>\n" : "")
                + toStringAnotherExpression(rightET)
                + (rightChildHasLowerPrecedence ? "<mo>)</mo>\n" : "")
                + "\n</mrow>\n"
                + "</mfrac>\n";

        return retVal;
    }

}
