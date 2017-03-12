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
import java.util.Map;
import org.cirdles.calamari.shrimp.IsotopeNames;
import org.cirdles.calamari.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring
 */
public class Ln extends Function {

    public Ln() {
        name = "ln";
        argumentCount = 1;
        precedence = 4;
        rowCount = 1;
        colCount = 1;
    }

    /**
     *
     * @param childrenET
     * @param pkInterpScan the value of pkInterpScan
     * @param isotopeToIndexMap the value of isotopeToIndexMap
     * @return the double
     */
    @Override
    public double eval(
            List<ExpressionTreeInterface> childrenET,
            double[] pkInterpScan,
            Map<IsotopeNames, Integer> isotopeToIndexMap) {
        double retVal;
        try {
//            retVal = StrictMath.log(childrenET.get(0).eval(pkInterpScan, isotopeToIndexMap));
            // Feb 2017 to cause replication of Squid2.5 results            
            retVal = Math.log(childrenET.get(0).eval(pkInterpScan, isotopeToIndexMap));
        } catch (Exception e) {
            retVal = 0.0;
        }

        return retVal;
    }

    /**
     *
     * @param childrenET the value of childrenET
     * @param shrimpFractions the value of shrimpFraction
     * @param pkInterpScan the value of pkInterpScan
     * @param isotopeToIndexMap the value of isotopeToIndexMap
     * @return the double[][]
     */
    @Override
    public double[][] eval2Array(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, double[] pkInterpScan, Map<IsotopeNames, Integer> isotopeToIndexMap) {

        double retVal;
        try {
//            retVal = StrictMath.log(childrenET.get(0).eval(pkInterpScan, isotopeToIndexMap));
            // Feb 2017 to cause replication of Squid2.5 results            
            retVal = Math.log(childrenET.get(0).eval2Array(shrimpFractions, pkInterpScan, isotopeToIndexMap)[0][0]);
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
        String retVal
                = "<mrow>"
                + "<mi>ln</mi>"
                + "<mfenced>";

        for (int i = 0; i < childrenET.size(); i++) {
            retVal += toStringAnotherExpression(childrenET.get(i)) + "&nbsp;\n";
        }

        retVal += "</mfenced></mrow>\n";

        return retVal;
    }

}
