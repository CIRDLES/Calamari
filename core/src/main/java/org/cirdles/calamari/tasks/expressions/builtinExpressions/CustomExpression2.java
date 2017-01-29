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
package org.cirdles.calamari.tasks.expressions.builtinExpressions;

import org.cirdles.calamari.shrimp.RawRatioNamesSHRIMP;
import org.cirdles.calamari.tasks.expressions.ExpressionTree;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeWithRatiosInterface;
import org.cirdles.calamari.tasks.expressions.operations.Operation;

/**
 *
 * @author James F. Bowring
 */
public class CustomExpression2 {

    /**
     * Squid Excel format is ln(["206/238"]) has EqNum = 1
     */
    public final static ExpressionTreeInterface EXPRESSION = new ExpressionTree("Ln206/238");

    static {
        ((ExpressionTreeWithRatiosInterface) EXPRESSION).getRatiosOfInterest().add(RawRatioNamesSHRIMP.r206_238w);
        ExpressionTreeInterface r206_238w = ExpressionTreeWithRatiosInterface.buildRatioExpression(RawRatioNamesSHRIMP.r206_238w);

        ((ExpressionTree) EXPRESSION).setLeftET(r206_238w);
        ((ExpressionTree) EXPRESSION).setRightET(null);
        ((ExpressionTree) EXPRESSION).setOperation(Operation.log());

        ((ExpressionTree) EXPRESSION).setRootExpressionTree(true);
    }
}
