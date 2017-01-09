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
import org.cirdles.calamari.tasks.expressions.ConstantNode;
import org.cirdles.calamari.tasks.expressions.ExpressionTree;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeWithRatiosInterface;
import org.cirdles.calamari.tasks.expressions.operations.Operation;

/**
 *
 * @author James F. Bowring
 */
public class SquidExpressionMinus1 {

    /**
     * Squid Excel format is ["206/238"]/["254/238"]^2 has EqNum = -1
     */
    public final static ExpressionTreeInterface EXPRESSION = new ExpressionTree("206/238 Calib Const");

    static {
        ((ExpressionTreeWithRatiosInterface) EXPRESSION).getRatiosOfInterest().add(RawRatioNamesSHRIMP.r206_238w);
        ExpressionTreeInterface r206_238w = ExpressionTreeWithRatiosInterface.buildRatioExpression(RawRatioNamesSHRIMP.r206_238w);

        ((ExpressionTreeWithRatiosInterface) EXPRESSION).getRatiosOfInterest().add(RawRatioNamesSHRIMP.r254_238w);
        ExpressionTreeInterface r254_238w = ExpressionTreeWithRatiosInterface.buildRatioExpression(RawRatioNamesSHRIMP.r254_238w);

        ExpressionTreeInterface r254_238wSquared = new ExpressionTree("254/238^2", r254_238w, new ConstantNode("2", 2.0), Operation.pow());

        ((ExpressionTree) EXPRESSION).setLeftET(r206_238w);
        ((ExpressionTree) EXPRESSION).setRightET(r254_238wSquared);
        ((ExpressionTree) EXPRESSION).setOperation(Operation.divide());

        ((ExpressionTree) EXPRESSION).setRootExpressionTree(true);
    }

}
