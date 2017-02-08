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
import java.util.Map;
import org.cirdles.calamari.shrimp.IsotopeNames;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring
 */
public class Pow extends Operation {

    public Pow() {
        name = "pow";
        precedence = 4;
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
            retVal = Math.pow(leftET.eval(pkInterpScan, isotopeToIndexMap), rightET.eval(pkInterpScan, isotopeToIndexMap));
        } catch (Exception e) {
            retVal = 0.0;
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
        String retVal
                = "<mrow>\n"
                + "<msup>\n"
                + "<mfenced>\n"
                + "<mrow>\n"
                + toStringAnotherExpression(leftET)
                + "</mrow>\n"
                + "</mfenced>\n"
                + rightET.toStringMathML()
                + "</msup>\n"
                + "</mrow>\n";

        return retVal;
    }

}
