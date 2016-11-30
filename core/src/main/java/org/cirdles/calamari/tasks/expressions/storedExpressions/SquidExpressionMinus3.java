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

import org.cirdles.calamari.shrimp.IsotopeNames;
import org.cirdles.calamari.shrimp.RawRatioNamesSHRIMP;
import org.cirdles.calamari.tasks.expressions.ExpressionTree;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class SquidExpressionMinus3 extends ExpressionTree {

    /**
     * Squid Excel format is (0.03446*["254/238"]+0.868)*["248/254"] has EqNum
     * =-3
     */
    public SquidExpressionMinus3() {
        super("232/238", 0.0);

        ratiosOfInterest.add(RawRatioNamesSHRIMP.r254_238w);
        ratiosOfInterest.add(RawRatioNamesSHRIMP.r248_254w);

        ExpressionTreeInterface species254 = new ShrimpSpeciesNode(IsotopeNames.UO254);
        ExpressionTreeInterface species238 = new ShrimpSpeciesNode(IsotopeNames.U238);
        ExpressionTreeInterface species248 = new ShrimpSpeciesNode(IsotopeNames.ThO248);

        ExpressionTreeInterface r254_238 = new ExpressionTree("254/238", 0.0, species254, species238, divide);
        ExpressionTreeInterface r248_254 = new ExpressionTree("248/254", 0.0, species248, species254, divide);

        ExpressionTreeInterface term1 = new ExpressionTree("0.03446 * 254/238", 0.0, new ExpressionTree("0.03446)", 0.03446), r254_238, multiply);
        ExpressionTreeInterface term2 = new ExpressionTree("0.03446 * 254/238 + 0.868", 0.0, term1, new ExpressionTree("0.868", 0.868), add);

        leftET = term2;
        rightET = r248_254;
        operation = multiply;
    }

}
