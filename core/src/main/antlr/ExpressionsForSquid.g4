/******************************************************
 * A multi-line Javadoc-like comment                  *
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
    CONSTANT
    | VARIABLE
    | ('-') expr
    | expr ('*'|'/') expr
    | expr ('+'|'-') expr
    | expr ('^') expr
    | '(' expr ')'
    | '[' expr ']'

    ;

fragment
Exponent : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

// some lexer rules
CONSTANT : [0-9]+['.']?[0-9]+Exponent? ; // match identifiers
VARIABLE : [a-zA-Z0-9_]+ ; // match Variable Names

WS
   : [ \r\n\t] + -> channel (HIDDEN)
   ;
