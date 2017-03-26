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

import java.util.List;
import org.cirdles.calamari.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring
 */
public class SqBiweight extends Function {

    /**
     * Provides the functionality of Squid's sqBiweight and biWt by calculating
     * TukeysBiweight and returning mean, sigma, and 95% confidence and encoding
     * the labels for each cell of the values array produced by eval2Array.
     *
     * @see
     * https://raw.githubusercontent.com/CIRDLES/LudwigLibrary/master/vbaCode/squid2.5Basic/Resistant.bas
     * @see
     * https://raw.githubusercontent.com/CIRDLES/LudwigLibrary/master/vbaCode/squid2.5Basic/StringUtils.bas
     */
    public SqBiweight() {
        name = "sqBiweight";
        argumentCount = 2;
        precedence = 4;
        rowCount = 1;
        colCount = 3;
        labelsForValues = new String[][]{{"Biwt Mean", "Biwt Sigma", "\u00B195%conf"}};
    }

    /**
     * Requires that child 0 is a VariableNode that evaluates to a double array
     * with one row and a column for each member of shrimpFractions and that
     * child 1 is a ConstantNode that evaluates to an integer.
     *
     * @param childrenET list containing child 0 and child 1
     * @param shrimpFractions a list of shrimpFractions
     * @return the double[1][3] array of mean, sigma, 95% confidence
     */
    @Override
    public double[][] eval2Array(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions) {

        double[][] retVal;
        try {
            double[] variableValues = childrenET.get(0).eval2Array(shrimpFractions)[0];
            double[] tuning = childrenET.get(1).eval2Array(shrimpFractions)[0];
            double[] tukeysBiweight = org.cirdles.ludwig.SquidMathUtils.tukeysBiweight(variableValues, tuning[0])[0];
            retVal = new double[][]{{tukeysBiweight[0], tukeysBiweight[1], tukeysBiweight[2]}};
        } catch (ArithmeticException e) {
            retVal = new double[][]{{0.0, 0.0, 0.0}};
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
                + "<mi>" + name + "</mi>"
                + "<mfenced>";

        for (int i = 0; i < childrenET.size(); i++) {
            retVal += toStringAnotherExpression(childrenET.get(i)) + "&nbsp;\n";
        }

        retVal += "</mfenced></mrow>\n";

        return retVal;
    }

}
