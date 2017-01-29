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
import org.cirdles.calamari.ExpressionsForSquidLexer;
import org.cirdles.calamari.ExpressionsForSquidParser;
import org.cirdles.calamari.ExpressionsForSquidParser.ExprContext;
import org.cirdles.calamari.tasks.expressions.ExpressionTree;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeBuilderInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.calamari.tasks.expressions.OperationOrFunctionInterface;
import org.cirdles.calamari.tasks.expressions.constants.ConstantNode;
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
        ExpressionsForSquidLexer lexer = new ExpressionsForSquidLexer(new ANTLRInputStream(expression));

        // Get a list of matched tokens
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Pass the tokens to the parser
        ExpressionsForSquidParser parser = new ExpressionsForSquidParser(tokens);

        // Specify our entry point
        ExprContext expSentenceContext = parser.expr();

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
            if (savedExp instanceof ExpressionTreeBuilderInterface) {
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

    private ExpressionTreeInterface walkTree(String token, ExpressionTreeInterface exp) {
        TokenTypes tokenType = TokenTypes.getType(token);
        ExpressionTreeInterface retExpTree = null;

        switch (tokenType) {
            case OPERATOR_A:
            case OPERATOR_M:
            case OPERATOR_E:
                OperationOrFunctionInterface operation = Operation.operationFactory(OPERATIONS_MAP.get(token));
                retExpTree = new ExpressionTree(operation);

                if (exp == null) {
                    // do nothing
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
                } else if (((ExpressionTreeBuilderInterface) exp).getRightET() == null) {
                    ((ExpressionTreeBuilderInterface) exp).setRightET(retExpTree);
                } else if (((ExpressionTreeBuilderInterface) exp).getLeftET() == null) {
                    ((ExpressionTreeBuilderInterface) exp).setLeftET(retExpTree);
                }

                break;

        }

        return retExpTree;
    }

    private void printTree(ExpressionsForSquidParser parser, ParseTree tree, List<String> parsed) {
        if (tree.getChildCount() < 1) {
            parsed.add(tree.toStringTree(parser));
        } else {
            for (int i = 0; i < tree.getChildCount(); i++) {
                printTree(parser, tree.getChild(i), parsed);
            }
        }
    }

}
