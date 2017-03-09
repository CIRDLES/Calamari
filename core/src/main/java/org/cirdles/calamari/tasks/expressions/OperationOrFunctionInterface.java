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
package org.cirdles.calamari.tasks.expressions;

import java.util.List;
import java.util.Map;
import org.cirdles.calamari.shrimp.IsotopeNames;

/**
 *
 * @author James F. Bowring
 */
public interface OperationOrFunctionInterface {

    public abstract double eval(List<ExpressionTreeInterface> childrenET, double[] pkInterpScan, Map<IsotopeNames, Integer> isotopeToIndexMap);
    
    public abstract double[][] eval2Array(List<ExpressionTreeInterface> childrenET, double[] pkInterpScan, Map<IsotopeNames, Integer> isotopeToIndexMap);

    /**
     * @return the precedence
     */
    public int getPrecedence();

    /**
     * @return the argumentCount
     */
    public int getArgumentCount();

    /**
     * @param argumentCount the argumentCount to set
     */
    public void setArgumentCount(int argumentCount);

    /**
     *
     * @param childrenET the value of childrenET
     * @return
     */
    public abstract String toStringMathML(
            List<ExpressionTreeInterface> childrenET);
}
