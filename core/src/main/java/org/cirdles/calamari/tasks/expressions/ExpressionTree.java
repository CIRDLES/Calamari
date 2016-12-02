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
package org.cirdles.calamari.tasks.expressions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.cirdles.calamari.shrimp.IsotopeNames;
import org.cirdles.calamari.shrimp.RawRatioNamesSHRIMP;
import org.cirdles.calamari.tasks.expressions.operations.Add;
import org.cirdles.calamari.tasks.expressions.operations.Divide;
import org.cirdles.calamari.tasks.expressions.operations.Log;
import org.cirdles.calamari.tasks.expressions.operations.Multiply;
import org.cirdles.calamari.tasks.expressions.operations.OperationInterface;
import org.cirdles.calamari.tasks.expressions.operations.Pow;
import org.cirdles.calamari.tasks.expressions.operations.Subtract;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class ExpressionTree implements ExpressionTreeInterface, ExpressionTreeWithRatiosInterface {
    
    protected String prettyName;
    protected double value;
    protected ExpressionTreeInterface leftET;
    protected ExpressionTreeInterface rightET;
    protected OperationInterface operation;
    protected List<RawRatioNamesSHRIMP> ratiosOfInterest;

    protected OperationInterface add;
    protected OperationInterface subtract;
    protected OperationInterface multiply;
    protected OperationInterface divide;
    protected OperationInterface log;
    protected OperationInterface pow;

    protected ExpressionTree() {
        this("EMPTY", 0.0);
    }

    public ExpressionTree(String prettyName, double value) {
        this(prettyName, value, null, null, null);
    }

    /**
     *
     * @param prettyName the value of prettyName
     * @param value the value of value
     * @param leftET the value of leftET
     * @param rightET the value of rightET
     * @param operation the value of operation
     */
    public ExpressionTree(String prettyName,
            double value,
            ExpressionTreeInterface leftET,
            ExpressionTreeInterface rightET,
            OperationInterface operation) {
        this(prettyName, value, leftET, rightET, operation, new ArrayList<RawRatioNamesSHRIMP>());
    }

    public ExpressionTree(String prettyName,
            double value,
            ExpressionTreeInterface leftET,
            ExpressionTreeInterface rightET,
            OperationInterface operation,
            List<RawRatioNamesSHRIMP> ratiosOfInterest) {
        this.prettyName = prettyName;
        this.value = value;
        this.leftET = leftET;
        this.rightET = rightET;
        this.operation = operation;
        this.ratiosOfInterest = ratiosOfInterest;

        add = new Add();
        subtract = new Subtract();
        multiply = new Multiply();
        divide = new Divide();
        log = new Log();
        pow = new Pow();

    }

    /**
     *
     * @param pkInterpScan the value of pkInterpScan
     * @param isotopeToIndexMap the value of isotopeToIndexMap
     * @return the double
     */
    @Override
    public double eval(double[] pkInterpScan, Map<IsotopeNames, Integer> isotopeToIndexMap) {
        return operation == null ? value : operation.eval(leftET, rightET, pkInterpScan, isotopeToIndexMap);
    }

    public Set extractUniqueSpeciesNumbers() {
        // assume acquisition order is atomic weight order
        Set<IsotopeNames> eqPkUndupeOrd = new TreeSet<>();
        for (int i = 0; i < ratiosOfInterest.size(); i++) {
            eqPkUndupeOrd.add(ratiosOfInterest.get(i).getNumerator());
            eqPkUndupeOrd.add(ratiosOfInterest.get(i).getDenominator());
        }
        return eqPkUndupeOrd;
    }

    /**
     * @return the prettyName
     */
    @Override
    public String getPrettyName() {
        return prettyName;
    }

    /**
     * @return the ratiosOfInterest
     */
    public List<RawRatioNamesSHRIMP> getRatiosOfInterest() {
        return ratiosOfInterest;
    }

}
