/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.calamari.tasks.expressions.operations;

import java.util.Map;
import org.cirdles.calamari.shrimp.IsotopeNames;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring
 */
public class Log implements OperationInterface {

    /**
     *
     * @param leftET the value of leftET
     * @param rightET the value of rightET
     * @param pkInterpScan the value of pkInterpScan
     * @param isotopeToIndexMap the value of isotopeToIndexMap
     * @return the double
     */
    @Override
    public double eval(
            ExpressionTreeInterface leftET,
            ExpressionTreeInterface rightET,
            double[] pkInterpScan,
            Map<IsotopeNames, Integer> isotopeToIndexMap) {
        double retVal;
        try {
            retVal = Math.log(leftET.eval(pkInterpScan, isotopeToIndexMap));
        } catch (Exception e) {
            retVal = 0.0;
        }

        return retVal;
    }

}
