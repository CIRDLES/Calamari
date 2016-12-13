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
package org.cirdles.calamari.tasks;

import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring
 */
public class TaskExpressionEvaluatedModel implements TaskExpressionEvaluatedModelInterface {

    private ExpressionTreeInterface expression;
    private double[] ratEqVal;
    private double[] ratEqTime;
    private double[] ratEqErr;

    private TaskExpressionEvaluatedModel() {
    }

    public TaskExpressionEvaluatedModel(ExpressionTreeInterface expression, double[] ratEqVal, double[] ratEqTime, double[] ratEqErr) {
        this.expression = expression;
        this.ratEqVal = ratEqVal.clone();
        this.ratEqTime = ratEqTime.clone();
        this.ratEqErr = ratEqErr.clone();
    }

    /**
     * @return the expression
     */
    @Override
    public ExpressionTreeInterface getExpression() {
        return expression;
    }

    /**
     * @return the ratEqVal
     */
    @Override
    public double[] getRatEqVal() {
        return ratEqVal.clone();
    }

    /**
     * @return the ratEqTime
     */
    @Override
    public double[] getRatEqTime() {
        return ratEqTime.clone();
    }

    /**
     * @return the ratEqErr
     */
    @Override
    public double[] getRatEqErr() {
        return ratEqErr.clone();
    }

}
