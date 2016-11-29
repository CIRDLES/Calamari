/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.calamari.tasks.expressions;

import org.cirdles.calamari.tasks.expressions.operations.Add;
import org.cirdles.calamari.tasks.expressions.operations.Divide;
import org.cirdles.calamari.tasks.expressions.operations.Log;
import org.cirdles.calamari.tasks.expressions.operations.Multiply;
import org.cirdles.calamari.tasks.expressions.operations.Subtract;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class Driver {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ExpressionTreeInterface expT = new ExpressionTree("NONAME", 0, new ExpressionTree("five", 5.), new ExpressionTree("six", 6.), new Add());
        ExpressionTreeInterface expT2 = new ExpressionTree("NONAME", 0, expT, expT, new Subtract());
        ExpressionTreeInterface expT3 = new ExpressionTree("NONAME", 0, expT, expT, new Multiply());
        ExpressionTreeInterface expT4 = new ExpressionTree("NONAME", 0, expT3, expT, new Divide());
        ExpressionTreeInterface expT5 = new ExpressionTree("NONAME", 0, expT3, null, new Log());

        System.out.println("RESULTS = " + expT.eval(null, null) + "  " + expT2.eval(null, null) + "  " + expT3.eval(null, null) + "  " + expT4.eval(null, null) + "  " + expT5.eval(null, null));
    }
}
