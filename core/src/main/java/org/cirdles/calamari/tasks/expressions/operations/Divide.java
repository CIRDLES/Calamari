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

package org.cirdles.calamari.tasks.expressions.operations;

import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class Divide implements OperationInterface {

    @Override
    public double eval(ExpressionTreeInterface leftET, ExpressionTreeInterface rightET) {
        double retVal = 0.0;
        try {
            retVal = leftET.eval() / rightET.eval();
        } catch (Exception e) {
        }
               
        return retVal;
    }

}
