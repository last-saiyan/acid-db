grammar Expression;
//modify for strings 'string'
prog:   expr+ ;


//stat:   expr NEWLINE                # printExpr
//    |   ID '=' expr NEWLINE         # assign
//    |   NEWLINE                     # blank
//    ;

expr:   expr op=('*'|'/') expr      # MulDiv
    |   expr op=('+'|'-') expr      # AddSub
    |   expr op=('='|'>='|'<='|'<'|'>') expr      # Equal
    |   INT                         # int
    |   ID                          # id
    |   '(' expr ')'                # parens
    ;

MUL :   '*' ; // assigns token name to '*' used above in grammar
DIV :   '/' ;
ADD :   '+' ;
SUB :   '-' ;

//GT: '>';
//LT: '<';
//GE: '>=';
//LE: '<=';
EQ: '=';

ID : [a-zA-Z]+;         // match fieldName

//ID  :   FIELD | STR ;      // match identifiers
//FIELD : [a-zA-Z]+;         // match fieldName
//STR : '\''[a-zA-Z]+'\'';   // match single quote string

//NUM :   INT | FLT;       // int or float
INT :   [0-9]+ ;         // match integers
//FLT :   [0-9]+'.'[0-9]+;  // float

WS  :   [ \t\n]+ -> skip ; // toss out whitespace
