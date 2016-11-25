/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.calamari.tasks.expressions.operations;

import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class Log implements OperationInterface {

    @Override
    public double eval(ExpressionTreeInterface leftET, ExpressionTreeInterface rightET) {
        double retVal = 0.0;
        try {
            retVal = Math.log(leftET.eval());
        } catch (Exception e) {
        }

        return retVal;
    }

}
