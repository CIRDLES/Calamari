/******************************************************
 * A multi-line Javadoc-like comment about my grammar *
 ******************************************************/
grammar ExpressionsForSquid;

@header {
    package org.cirdles.calamari;
} 


// Monoline comment about a parser rule
//myStartingRule : stat+ ;

/* 
 A multi-line Java-like comment
 */
expr:
    expr ('*'|'/') expr
    | expr ('+'|'-') expr
    | expr ('^') expr
    | CONSTANT
    | VARIABLE
    | '(' expr ')'
    | '[' expr ']'

    ;

// some lexer rules
CONSTANT : [a-zA-Z]+ ; // match identifiers
VARIABLE : [0-9]+ ; // match integers
//NEWLINE:'\r'? '\n' ; // return newlines to parser

WS
   : [ \r\n\t] + -> channel (HIDDEN)
   ;
