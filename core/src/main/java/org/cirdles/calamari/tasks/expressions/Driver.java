/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.calamari.tasks.expressions;

import org.cirdles.calamari.shrimp.RawRatioNamesSHRIMP;
import org.cirdles.calamari.tasks.expressions.operations.Add;
import org.cirdles.calamari.tasks.expressions.operations.Divide;
import org.cirdles.calamari.tasks.expressions.operations.Log;
import org.cirdles.calamari.tasks.expressions.operations.Multiply;
import org.cirdles.calamari.tasks.expressions.operations.Operation;
import org.cirdles.calamari.tasks.expressions.operations.Subtract;

/**
 *
 * @author James F. Bowring
 */
public class Driver {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ExpressionTreeInterface expT = new ExpressionTree("NONAME", new ExpressionTree("five"), new ExpressionTree("six"), new Add());
        ExpressionTreeInterface expT2 = new ExpressionTree("NONAME", expT, expT, new Subtract());
        ExpressionTreeInterface expT3 = new ExpressionTree("NONAME", expT, expT, new Multiply());
        ExpressionTreeInterface expT4 = new ExpressionTree("NONAME", expT3, expT, new Divide());
        ExpressionTreeInterface expT5 = new ExpressionTree("NONAME", expT3, null, new Log());

        ExpressionTreeInterface EXPRESSION = new ExpressionTree("206/238 Calib Const");
        ((ExpressionTreeWithRatiosInterface) EXPRESSION).getRatiosOfInterest().add(RawRatioNamesSHRIMP.r206_238w);
        ExpressionTreeInterface r206_238w = ExpressionTreeWithRatiosInterface.buildRatioExpression(RawRatioNamesSHRIMP.r206_238w);

        ((ExpressionTreeWithRatiosInterface) EXPRESSION).getRatiosOfInterest().add(RawRatioNamesSHRIMP.r254_238w);
        ExpressionTreeInterface r254_238w = ExpressionTreeWithRatiosInterface.buildRatioExpression(RawRatioNamesSHRIMP.r254_238w);

        ExpressionTreeInterface r254_238wSquared = new ExpressionTree("254/238^2", r254_238w, new ConstantNode("2", 2.0), Operation.pow());

        ((ExpressionTree) EXPRESSION).setLeftET(r206_238w);
        ((ExpressionTree) EXPRESSION).setRightET(r254_238wSquared);
        ((ExpressionTree) EXPRESSION).setOperation(Operation.divide());

        ((ExpressionTree) EXPRESSION).setRootExpressionTree(true);

        System.out.println(EXPRESSION.toStringMathML());

//        System.out.println("RESULTS = " + expT.eval(null, null) + "  " + expT2.eval(null, null) + "  " + expT3.eval(null, null) + "  " + expT4.eval(null, null) + "  " + expT5.eval(null, null));
    }
}
