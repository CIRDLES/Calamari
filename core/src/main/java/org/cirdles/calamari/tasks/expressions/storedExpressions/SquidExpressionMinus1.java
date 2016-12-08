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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.cirdles.calamari.shrimp.RawRatioNamesSHRIMP;
import org.cirdles.calamari.tasks.expressions.ConstantNode;
import org.cirdles.calamari.tasks.expressions.ExpressionTree;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring
 */
public class SquidExpressionMinus1 extends ExpressionTree {

    /**
     * Squid Excel format is ["206/238"]/["254/238"]^2 has EqNum = -1
     */
    public SquidExpressionMinus1() {
        super("206/238 Calib Const");

        ratiosOfInterest.add(RawRatioNamesSHRIMP.r206_238w);
        ExpressionTreeInterface r206_238w = buildRatioExpression(RawRatioNamesSHRIMP.r206_238w);

        ratiosOfInterest.add(RawRatioNamesSHRIMP.r254_238w);
        ExpressionTreeInterface r254_238w = buildRatioExpression(RawRatioNamesSHRIMP.r254_238w);

        ExpressionTreeInterface r254_238wSquared = new ExpressionTree("254/238^2", r254_238w, new ConstantNode("2", 2.0), pow);

        leftET = r206_238w;
        rightET = r254_238wSquared;
        operation = divide;
    }

    public static void main(String[] args) {

        XStream xstream = new XStream(new DomDriver());

        String xml = xstream.toXML(new SquidExpressionMinus1());

        System.out.print(xml);
    }

}
