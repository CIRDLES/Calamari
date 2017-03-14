/* 
 * Copyright 2006-2017 CIRDLES.org.
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

import java.util.List;
import org.cirdles.calamari.shrimp.ShrimpFractionExpressionInterface;

/**
 *
 * @author James F. Bowring
 */
public interface ExpressionTreeInterface {

    /**
     *
     * @param shrimpFractions the value of shrimpFraction
     * @return the double[][]
     */
    public double[][] eval2Array(List<ShrimpFractionExpressionInterface> shrimpFractions);

    public String getName();

    /**
     * @return the parentET
     */
    public ExpressionTreeInterface getParentET();

    /**
     * @param parentET the parentET to set
     */
    public void setParentET(ExpressionTreeInterface parentET);

    /**
     * @return the rootExpressionTree
     */
    public boolean isRootExpressionTree();

    public String toStringMathML();

    public boolean isTypeFunction();

    public boolean isTypeFunctionOrOperation();

    public int argumentCount();

}
