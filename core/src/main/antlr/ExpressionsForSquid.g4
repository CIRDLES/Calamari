/******************************************************
 * A multi-line Javadoc-like comment                  *
 ******************************************************/
grammar ExpressionsForSquid;

@header {
    package org.cirdles.calamari;
} 

expr:
    function
    | IDENT
    | VARIABLE
    | ('-') expr
    | expr ('*'|'/') expr
    | expr ('+'|'-') expr
    | expr ('^') expr
    | '(' expr ')'
    | '[' expr ']'

    ;

argumentList: expr (',' expr)*;

function: IDENT '(' argumentList ')';


fragment
Exponent : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

// some lexer rules
CONSTANT : [0-9]+['.']?[0-9]+Exponent? ; // match identifiers
VARIABLE : [a-zA-Z0-9_]+ ; // match Variable Names


COMMENT:                '//' ~[\r\n]* -> skip;
COMMENT_DELIMITED:      '/*' .* '*/' -> skip;
Integer:                '0' | ([1-9][0-9]*);
Float:                  ('0' | ([1-9][0-9]*)) ('.' [0-9]*)?;
IDENT:             [a-zA-Z_][a-zA-Z0-9_]*;


WS
   : [ \r\n\t] + -> channel (HIDDEN)
   ;
