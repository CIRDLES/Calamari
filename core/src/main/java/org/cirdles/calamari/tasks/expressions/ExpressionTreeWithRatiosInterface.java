/*
 * Copyright 2006-2016 CIRDLES.org.
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
import java.util.Set;
import org.cirdles.calamari.shrimp.RawRatioNamesSHRIMP;
import org.cirdles.calamari.tasks.expressions.operations.Divide;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public interface ExpressionTreeWithRatiosInterface {

    /**
     * @return the ratiosOfInterest
     */
    public List<RawRatioNamesSHRIMP> getRatiosOfInterest();

    public Set extractUniqueSpeciesNumbers();

    public default ExpressionTreeInterface buildRatioExpression(RawRatioNamesSHRIMP ratio) {
        return new ExpressionTree(
                ratio.getDisplayName(),
                0.0, 
                new ShrimpSpeciesNode(ratio.getNumerator()), 
                new ShrimpSpeciesNode(ratio.getDenominator()), 
                new Divide());
    }
}
