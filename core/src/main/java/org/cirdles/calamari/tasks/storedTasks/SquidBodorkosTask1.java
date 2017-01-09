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
package org.cirdles.calamari.tasks.storedTasks;

import org.cirdles.calamari.tasks.Task;
import org.cirdles.calamari.tasks.expressions.ExpressionTree;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.calamari.tasks.expressions.builtinExpressions.CustomExpression1;
import org.cirdles.calamari.tasks.expressions.builtinExpressions.CustomExpression2;
import org.cirdles.calamari.tasks.expressions.builtinExpressions.SquidExpressionMinus1;
import org.cirdles.calamari.tasks.expressions.builtinExpressions.SquidExpressionMinus3;
import org.cirdles.calamari.tasks.expressions.builtinExpressions.SquidExpressionMinus4;
import org.cirdles.calamari.utilities.xmlSerialization.XMLSerializerInterface;

/**
 *
 * @author James F. Bowring
 */
public class SquidBodorkosTask1 extends Task {

    public SquidBodorkosTask1() {
        super("SquidBodorkosTask1");
        taskExpressionsOrdered.add(CustomExpression1.EXPRESSION);
        taskExpressionsOrdered.add(CustomExpression2.EXPRESSION);
        taskExpressionsOrdered.add(SquidExpressionMinus1.EXPRESSION);
        taskExpressionsOrdered.add(SquidExpressionMinus4.EXPRESSION);
        taskExpressionsOrdered.add(SquidExpressionMinus3.EXPRESSION);

        // experiment
        ((XMLSerializerInterface) SquidExpressionMinus3.EXPRESSION).serializeXMLObject(SquidExpressionMinus3.EXPRESSION, "SquidExpressionMinus3.xml");
        ExpressionTreeInterface test = new ExpressionTree();
        test = (ExpressionTreeInterface)((XMLSerializerInterface) test).readXMLObject("SquidExpressionMinus3.xml", false);
        ((ExpressionTree) test).setName("TESTSquidExpressionMinus3");
        ((ExpressionTree) test).setRootExpressionTree(true);
        taskExpressionsOrdered.add(test);
    }
}
