/** Simple statically-typed programming language with functions and variables
 *  taken from "Language Implementation Patterns" book.
 * https://media.pragprog.com/titles/tpantlr2/code/examples/Cymbol.g4
 */
grammar ExpressionsForSquid2;

@header {
    package org.cirdles.calamari;
} 

file:   (functionDecl | varDecl)+ ;

varDecl
    :   type ID ('=' expr)? ';'
    ;
type:   'float' | 'int' | 'void' ; // user-defined types

functionDecl
    :   type FUNCTION '(' formalParameters? ')' block // "void f(int x) {...}"
    ;
formalParameters
    :   formalParameter (',' formalParameter)*
    ;
formalParameter
    :   type ID
    ;

block:  '{' stat* '}' ;   // possibly empty statement block
stat:   block
    |   varDecl
    |   'if' expr 'then' stat ('else' stat)?
    |   'return' expr? ';' 
    |   expr '=' expr ';' // assignment
    |   expr ';'          // func call
    ;

expr:   FUNCTION '(' exprList? ')'    // func call like f(), f(x), f(1,2)
    |   ID '[' expr ']'         // array index like a[i], a[i][j]
    |   '-' expr                // unary minus
    |   '!' expr                // boolean not
    |   expr ('*'|'/') expr
    |   expr ('+'|'-') expr
    |   expr ('^') expr
    |   expr '==' expr          // equality comparison (lowest priority op)
    |   ID                      // variable reference
    |   INT
    |   FLOAT
    |   '(' expr ')'
    ;
exprList : expr (',' expr)* ;   // arg list

FUNCTION : 'ln' ;
ID  :   LETTER (LETTER | [0-9])* ;
fragment
LETTER : [a-zA-Z] ;


INT :   [0-9]+ ;

INTEGER:                '0' | ([1-9][0-9]*);

FLOAT :              ('0' | ([1-9][0-9]*)) ('.' [0-9]*)? Exponent? ;
 
fragment
Exponent : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;


WS  :   [ \t\n\r]+ -> skip ;

SL_COMMENT
    :   '//' .*? '\n' -> skip
    ;