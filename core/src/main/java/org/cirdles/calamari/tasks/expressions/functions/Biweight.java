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

import java.math.BigDecimal;
import java.util.List;
import org.cirdles.calamari.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring
 */
public class Biweight extends Function {

    public Biweight() {
        name = "Biweight";
        argumentCount = 2;
        precedence = 4;
        rowCount = 1;
        colCount = 2;
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

        double[][] retVal;
        try {
            // assume child 0 is a VariableNode
            // assume child 1 is a number
            double[][] variableValues = childrenET.get(0).eval2Array(shrimpFractions);
            double [][] tuning = childrenET.get(1).eval2Array(shrimpFractions);
            BigDecimal[] retValBD = org.cirdles.ludwig.TukeyBiweight.biweightMean(variableValues[0], tuning[0][0]);
            retVal = new double[][]{{retValBD[0].doubleValue(), retValBD[1].doubleValue()}};
        } catch (Exception e) {
            retVal = new double[][]{{0.0, 0.0}};
        }

        return retVal;
    }

    /**
     *
     * @param childrenET the value of childrenET
     * @return
     */
    @Override
    public String toStringMathML(List<ExpressionTreeInterface> childrenET) {
        String retVal
                = "<mrow>"
                + "<mi>RobReg</mi>"
                + "<mfenced>";

        for (int i = 0; i < childrenET.size(); i++) {
            retVal += toStringAnotherExpression(childrenET.get(i)) + "&nbsp;\n";
        }

        retVal += "</mfenced></mrow>\n";

        return retVal;
    }

}
