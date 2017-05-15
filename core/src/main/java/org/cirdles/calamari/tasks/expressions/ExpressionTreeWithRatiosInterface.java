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

import org.cirdles.calamari.tasks.expressions.isotopes.ShrimpSpeciesNode;
import java.util.List;
import java.util.Set;
import org.cirdles.calamari.shrimp.RawRatioNamesSHRIMP;
import org.cirdles.calamari.tasks.expressions.operations.Operation;

/**
 *
 * @author James F. Bowring
 */
public interface ExpressionTreeWithRatiosInterface {

    /**
     * @return the ratiosOfInterest
     */
    public List<RawRatioNamesSHRIMP> getRatiosOfInterest();

    /**
     *
     * @return
     */
    public Set extractUniqueSpeciesNumbers();

    /**
     *
     * @param ratio
     * @return
     */
    public static ExpressionTreeInterface buildRatioExpression(RawRatioNamesSHRIMP ratio) {
        ExpressionTreeInterface ratioExpression
                = new ExpressionTree(
                        ratio.getDisplayNameNoSpaces(),
                        new ShrimpSpeciesNode(ratio.getNumerator(), "getPkInterpScanArray"),
                        new ShrimpSpeciesNode(ratio.getDenominator(), "getPkInterpScanArray"),
                        Operation.divide());
        
        ((ExpressionTreeWithRatiosInterface) ratioExpression).getRatiosOfInterest().add(ratio);
        return ratioExpression;
    }
}
