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
        argumentCount = 2;
        precedence = 4;
    }

    /**
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
            retVal = Math.pow(childrenET.get(0).eval(pkInterpScan, isotopeToIndexMap), childrenET.get(1).eval(pkInterpScan, isotopeToIndexMap));
        } catch (Exception e) {
            retVal = 0.0;
        }

        return retVal;
    }

    @Override
    public double[][] eval2Array(List<ExpressionTreeInterface> childrenET, double[] pkInterpScan, Map<IsotopeNames, Integer> isotopeToIndexMap) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    /**
     *
     * @param childrenET the value of childrenET
     * @return
     */
    @Override
    public String toStringMathML(List<ExpressionTreeInterface> childrenET) {
        String retVal
                = "<mrow>\n"
                + "<msup>\n"
                + "<mfenced>\n"
                + "<mrow>\n"
                + toStringAnotherExpression(childrenET.get(0))
                + "</mrow>\n"
                + "</mfenced>\n"
                + childrenET.get(1).toStringMathML()
                + "</msup>\n"
                + "</mrow>\n";

        return retVal;
    }

}
