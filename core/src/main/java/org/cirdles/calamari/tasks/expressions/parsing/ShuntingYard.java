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
import java.util.List;
import java.util.Stack;

/**
 * @author James F. Bowring
 */
public class ShuntingYard {

    public static void main(String[] args) {

        // Input: 3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3
        List<String> infixList = new ArrayList<>();
        infixList.add("3");
        infixList.add("+");
        infixList.add("4");
        infixList.add("*");
        infixList.add("2");
        infixList.add("/");
        infixList.add("(");
        infixList.add("1");
        infixList.add("-");
        infixList.add("5");
        infixList.add(")");
        infixList.add("^");
        infixList.add("2");
        infixList.add("^");
        infixList.add("3");

        System.out.println("Shunt " + infixToPostfix(infixList));
    }

    /**
     * @see https://en.wikipedia.org/wiki/Shunting-yard_algorithm
     * @param infix
     * @return
     */
    public static List<String> infixToPostfix(List<String> infix) {
        Stack<String> operatorStack = new Stack<>();
        List<String> outputQueue = new ArrayList<>();

        for (String token : infix) {
            // classify token
            TokenTypes tokenType = TokenTypes.getType(token);
            switch (tokenType) {
                case OPERATOR_A:
                    /* while there is an operator token o2, at the top of the operator stack and either
                    o1 is left-associative and its precedence is less than or equal to that of o2, or
                    o1 is right associative, and has precedence less than that of o2,
                    pop o2 off the operator stack, onto the output queue;
                    at the end of iteration push o1 onto the operator stack.
                     */
                    boolean keepLooking = true;
                    while (!operatorStack.empty() && keepLooking) {
                        TokenTypes peek = TokenTypes.getType(operatorStack.peek());
                        if ((peek.compareTo(TokenTypes.OPERATOR_A) == 0)
                                || (peek.compareTo(TokenTypes.OPERATOR_M) == 0)
                                || (peek.compareTo(TokenTypes.OPERATOR_E) == 0)) {
                            outputQueue.add(operatorStack.pop());
                        } else {
                            keepLooking = false;
                        }
                    }
                    operatorStack.push(token);
                    break;
                case OPERATOR_M:
                    /* while there is an operator token o2, at the top of the operator stack and either
                    o1 is left-associative and its precedence is less than or equal to that of o2, or
                    o1 is right associative, and has precedence less than that of o2,
                    pop o2 off the operator stack, onto the output queue;
                    at the end of iteration push o1 onto the operator stack.
                     */
                    keepLooking = true;
                    while (!operatorStack.empty() && keepLooking) {
                        TokenTypes peek = TokenTypes.getType(operatorStack.peek());
                        if ((peek.compareTo(TokenTypes.OPERATOR_M) == 0)
                                || (peek.compareTo(TokenTypes.OPERATOR_E) == 0)) {
                            outputQueue.add(operatorStack.pop());
                        } else {
                            keepLooking = false;
                        }
                    }
                    operatorStack.push(token);
                    break;
                case OPERATOR_E:
                    /* while there is an operator token o2, at the top of the operator stack and either
                    o1 is left-associative and its precedence is less than or equal to that of o2, or
                    o1 is right associative, and has precedence less than that of o2,
                    pop o2 off the operator stack, onto the output queue;
                    at the end of iteration push o1 onto the operator stack.
                     */
                    operatorStack.push(token);
                    break;
                case LEFT_PAREN:
                    operatorStack.push(token);
                    break;
                case RIGHT_PAREN:
                    /* Until the token at the top of the stack is a left parenthesis, pop operators off the stack onto the output queue.
                    Pop the left parenthesis from the stack, but not onto the output queue.
                    If the token at the top of the stack is a function token, pop it onto the output queue.
                    If the stack runs out without finding a left parenthesis, then there are mismatched parentheses.
                     */
                    keepLooking = true;
                    while (!operatorStack.empty() && keepLooking) {
                        TokenTypes peek = TokenTypes.getType(operatorStack.peek());
                        if ((peek.compareTo(TokenTypes.OPERATOR_A) == 0)
                                || (peek.compareTo(TokenTypes.OPERATOR_M) == 0)
                                || (peek.compareTo(TokenTypes.OPERATOR_E) == 0)) {
                            outputQueue.add(operatorStack.pop());
                        } else {
                            keepLooking = false;
                            if (peek.compareTo(TokenTypes.LEFT_PAREN) == 0) {
                                operatorStack.pop();
                            }
                        }
                    }
                    break;
                case CONSTANT:
                    outputQueue.add(token);
                    break;
                case VARIABLE:
                    outputQueue.add(token);
                    break;
                case FUNCTION:
                    operatorStack.push(token);
                    break;
                case COMMA:
                    /*If the token is a function argument separator (e.g., a comma):
                        Until the token at the top of the stack is a left parenthesis, 
                    pop operators off the stack onto the output queue. If no left parentheses 
                    are encountered, either the separator was misplaced or parentheses were mismatched.
                     */
                    break;
                default:
                    break;
            }
        }

        while (!operatorStack.empty()) {
            outputQueue.add(operatorStack.pop());
        }

        return outputQueue;
    }

    public enum TokenTypes {
        OPERATOR_A,
        OPERATOR_M,
        OPERATOR_E,
        LEFT_PAREN,
        RIGHT_PAREN,
        CONSTANT,
        VARIABLE,
        FUNCTION,
        COMMA;

        private TokenTypes() {
        }

        public static TokenTypes getType(String token) {
            TokenTypes retVal = VARIABLE;

            if ("+-".contains(token)) {
                retVal = OPERATOR_A;
            } else if ("*/".contains(token)) {
                retVal = OPERATOR_M;
            } else if ("^".contains(token)) {
                retVal = OPERATOR_E;
            } else if ("([".contains(token)) {
                retVal = LEFT_PAREN;
            } else if (")]".contains(token)) {
                retVal = RIGHT_PAREN;
            } else if (token.equals(",")) {
                retVal = COMMA;
            } else if ("Functions".contains(token)) {
                retVal = FUNCTION;
            } else {
                try {
                    Double number = Double.parseDouble(token);
                    retVal = CONSTANT;
                } catch (NumberFormatException numberFormatException) {
                }
            }

            return retVal;
        }
    }
}
