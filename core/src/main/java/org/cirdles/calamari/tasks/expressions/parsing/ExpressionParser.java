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
package org.cirdles.calamari.tasks.expressions.parsing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cirdles.calamari.ExpressionsForSquid2Lexer;
import org.cirdles.calamari.ExpressionsForSquid2Parser;
import org.cirdles.calamari.tasks.expressions.ExpressionTree;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeBuilderInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.calamari.tasks.expressions.OperationOrFunctionInterface;
import org.cirdles.calamari.tasks.expressions.constants.ConstantNode;
import org.cirdles.calamari.tasks.expressions.functions.Function;
import org.cirdles.calamari.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.calamari.tasks.expressions.operations.Operation;
import org.cirdles.calamari.tasks.expressions.parsing.ShuntingYard.TokenTypes;

/**
 *
 * @author James F. Bowring
 */
public class ExpressionParser {

    public ExpressionTreeInterface parseExpression(String expression) {
        // Get our lexer
        ExpressionsForSquid2Lexer lexer = new ExpressionsForSquid2Lexer(new ANTLRInputStream(expression));

        // Get a list of matched tokens
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Pass the tokens to the parser
        ExpressionsForSquid2Parser parser = new ExpressionsForSquid2Parser(tokens);

        // Specify our entry point
        ExpressionsForSquid2Parser.ExprContext expSentenceContext = parser.expr();

        parser.setBuildParseTree(true);
        List<ParseTree> children = expSentenceContext.children;

        List<String> parsed = new ArrayList<>();
        List<String> parsedRPN = new ArrayList<>();

        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                printTree(parser, children.get(i), parsed);
            }
            parsedRPN = ShuntingYard.infixToPostfix(parsed);

        }

        Collections.reverse(parsedRPN);

        return buildTree(parsedRPN);

    }

    public final static Map<String, String> OPERATIONS_MAP = new HashMap<>();

    static {

        OPERATIONS_MAP.put("+", "add");
        OPERATIONS_MAP.put("-", "subtract");
        OPERATIONS_MAP.put("/", "divide");
        OPERATIONS_MAP.put("*", "multiply");
        OPERATIONS_MAP.put("^", "pow");
    }

    public final static Map<String, String> FUNCTIONS_MAP = new HashMap<>();

    static {

        FUNCTIONS_MAP.put("ln", "ln");
        FUNCTIONS_MAP.put("Ln", "ln");
        FUNCTIONS_MAP.put("sqrt", "sqrt");
        FUNCTIONS_MAP.put("Sqrt", "sqrt");
        FUNCTIONS_MAP.put("exp", "exp");
        FUNCTIONS_MAP.put("Exp", "exp");
    }

    private ExpressionTreeInterface buildTree(List<String> parsedRPNreversed) {
        Iterator<String> parsedRPNreversedIterator = parsedRPNreversed.iterator();

        ExpressionTreeInterface exp = null;
        ExpressionTreeInterface savedExp = null;

        boolean firstPass = true;
        while (parsedRPNreversedIterator.hasNext()) {
            String token = parsedRPNreversedIterator.next();

            if (exp != null) {
                // find next available empty left
                exp = walkUpTreeToEmptyLeftChild(exp);
            }

            exp = walkTree(token, exp);
            if (firstPass) {
                savedExp = exp;
                firstPass = false;
            }
        }

        return savedExp;
    }

    private ExpressionTreeInterface walkUpTreeToEmptyLeftChild(ExpressionTreeInterface exp) {
        ExpressionTreeInterface savedExp = exp;
        ExpressionTreeInterface expParent = exp;

        boolean didAscend = true;
        while (didAscend) {
            if ((savedExp instanceof ExpressionTreeBuilderInterface)
                    && (!savedExp.isTypeFunction())) {
                if (((ExpressionTreeBuilderInterface) savedExp).getLeftET() != null) {
                    expParent = savedExp.getParentET();
                    savedExp = expParent;
                } else {
                    didAscend = false;
                }
            } else if (savedExp instanceof ConstantNode) {
                expParent = savedExp.getParentET();
                savedExp = expParent;
            } else if (savedExp instanceof ShrimpSpeciesNode) {
                expParent = savedExp.getParentET();
                savedExp = expParent;
            } else {
                didAscend = false;
            }
        }

        return expParent;
    }

    private ExpressionTreeInterface walkTree(String token, ExpressionTreeInterface myExp) {
        TokenTypes tokenType = TokenTypes.getType(token);
        ExpressionTreeInterface exp = myExp;

        if (exp != null) {
            if (exp.isTypeFunctionOrOperator()) {
                while (exp.argumentCount() == ((ExpressionTreeBuilderInterface) exp).getCountOfChildren()
                        && !exp.isRootExpressionTree()) {
                    exp = exp.getParentET();
                }
            }
        }

        ExpressionTreeInterface retExpTree = null;

        switch (tokenType) {
            case OPERATOR_A:
            case OPERATOR_M:
            case OPERATOR_E:
                OperationOrFunctionInterface operation = Operation.operationFactory(OPERATIONS_MAP.get(token));
                retExpTree = new ExpressionTree(operation);

                if (exp == null) {
                    // do nothing
                } else if (exp.isTypeFunction()) {
                    ((ExpressionTreeBuilderInterface) exp).setLeftET(retExpTree);
                } else if (((ExpressionTreeBuilderInterface) exp).getRightET() == null) {
                    ((ExpressionTreeBuilderInterface) exp).setRightET(retExpTree);
                } else if (((ExpressionTreeBuilderInterface) exp).getLeftET() == null) {
                    ((ExpressionTreeBuilderInterface) exp).setLeftET(retExpTree);
                }

                break;

            case FUNCTION:
                OperationOrFunctionInterface function = Function.operationFactory(FUNCTIONS_MAP.get(token));
                retExpTree = new ExpressionTree(function);

                if (exp == null) {
                    // do nothing
                } else if (exp.isTypeFunction()) {
                    ((ExpressionTreeBuilderInterface) exp).setLeftET(retExpTree);
                } else if (((ExpressionTreeBuilderInterface) exp).getRightET() == null) {
                    ((ExpressionTreeBuilderInterface) exp).setRightET(retExpTree);
                } else if (((ExpressionTreeBuilderInterface) exp).getLeftET() == null) {
                    ((ExpressionTreeBuilderInterface) exp).setLeftET(retExpTree);
                }

                break;

            case CONSTANT:
                retExpTree = new ConstantNode(token, Double.parseDouble(token));

                if (exp == null) {
                    // do nothing
                } else if (exp.isTypeFunction()) {
                    ((ExpressionTreeBuilderInterface) exp).setLeftET(retExpTree);
                } else if (((ExpressionTreeBuilderInterface) exp).getRightET() == null) {
                    ((ExpressionTreeBuilderInterface) exp).setRightET(retExpTree);
                } else if (((ExpressionTreeBuilderInterface) exp).getLeftET() == null) {
                    ((ExpressionTreeBuilderInterface) exp).setLeftET(retExpTree);
                }

                break;

            case VARIABLE:
                retExpTree = new ConstantNode(token, 0.0);

                if (exp == null) {
                    // do nothing
                } else if (exp.isTypeFunction()) {
                    ((ExpressionTreeBuilderInterface) exp).setLeftET(retExpTree);
                } else if (((ExpressionTreeBuilderInterface) exp).getRightET() == null) {
                    ((ExpressionTreeBuilderInterface) exp).setRightET(retExpTree);
                } else if (((ExpressionTreeBuilderInterface) exp).getLeftET() == null) {
                    ((ExpressionTreeBuilderInterface) exp).setLeftET(retExpTree);
                }

                break;

        }

        return retExpTree;
    }

    private void printTree(ExpressionsForSquid2Parser parser, ParseTree tree, List<String> parsed) {
        if (tree.getChildCount() < 1) {
            parsed.add(tree.toStringTree(parser));
        } else {
            for (int i = 0; i < tree.getChildCount(); i++) {
                printTree(parser, tree.getChild(i), parsed);
            }
        }
    }

}
