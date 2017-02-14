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
public class Pexp extends Operation {

    public Pexp() {
        name = "exp";
        argumentCount = 1;
        precedence = 4;//todo: rethink
    }

    /**
     * Denotes an expression to be wrapped in parentheses using only the
     * leftChild
     *
     * @param childrenET
     * @param pkInterpScan
     * @param isotopeToIndexMap
     * @return
     */
    @Override
    public double eval(
            List<ExpressionTreeInterface> childrenET,
            double[] pkInterpScan,
            Map<IsotopeNames, Integer> isotopeToIndexMap) {
        double retVal;
        try {
            retVal = childrenET.get(0).eval(pkInterpScan, isotopeToIndexMap);
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
     * @return 
     */
    @Override
    public String toStringMathML(ExpressionTreeInterface leftET, ExpressionTreeInterface rightET, List<ExpressionTreeInterface> childrenET) {
        String retVal
                = "<mrow>\n"
                + "<mo>(</mo>\n"
                + toStringAnotherExpression(leftET)
                + "<mo>)</mo>\n"
                + "</mrow>\n";

        return retVal;
    }

}
