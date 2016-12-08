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
package org.cirdles.calamari.tasks.expressions.storedExpressions;

import org.cirdles.calamari.shrimp.RawRatioNamesSHRIMP;
import org.cirdles.calamari.tasks.expressions.ConstantNode;
import org.cirdles.calamari.tasks.expressions.ExpressionTree;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring
 */
public class SquidExpressionMinus3 extends ExpressionTree {

    /**
     * Squid Excel format is (0.03446*["254/238"]+0.868)*["248/254"] EqNum -3
     */
    public SquidExpressionMinus3() {
        super("232/238");

        ratiosOfInterest.add(RawRatioNamesSHRIMP.r254_238w);
        ExpressionTreeInterface r254_238w = buildRatioExpression(RawRatioNamesSHRIMP.r254_238w);

        ratiosOfInterest.add(RawRatioNamesSHRIMP.r248_254w);
        ExpressionTreeInterface r248_254w = buildRatioExpression(RawRatioNamesSHRIMP.r248_254w);

        ExpressionTreeInterface term1 = new ExpressionTree("0.03446 * 254/238", new ConstantNode("0.03446)", 0.03446), r254_238w, multiply);
        ExpressionTreeInterface term2 = new ExpressionTree("0.03446 * 254/238 + 0.868", term1, new ConstantNode("0.868", 0.868), add);

        leftET = term2;
        rightET = r248_254w;
        operation = multiply;
    }

}
