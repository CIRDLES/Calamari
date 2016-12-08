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
import org.cirdles.calamari.tasks.expressions.ExpressionTree;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring
 */
public class CustomExpression2 extends ExpressionTree {

    /**
     * Squid Excel format is ln(["254/238"]) has EqNum = 1 
     */
    public CustomExpression2() {
        super("Ln206/238");

        ratiosOfInterest.add(RawRatioNamesSHRIMP.r206_238w);
        ExpressionTreeInterface r206_238w = buildRatioExpression(RawRatioNamesSHRIMP.r206_238w);
        
        leftET = r206_238w;
        rightET = null;
        operation = log;
    }

}
