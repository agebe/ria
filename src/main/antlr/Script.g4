// Parser rules start with a lower case letter
// Lexer rules start with a capital letter
grammar Script;
@header {
package io.github.agebe.script.antlr;
}
// TODO function definition
script: stmt*;
// TODO if statement
// TODO for statement
// TODO while statement
// TODO new operator
// TODO arithmetic operators
// TODO scopes {}
// https://docs.oracle.com/javase/tutorial/java/nutsandbolts/expressions.html
stmt: (expr | vardef  | varAssignStmt | returnStmt) ';';
returnStmt: 'return' expr? ;
vardef: 'var' ident assignment?;
varAssignStmt: ident assignment;
assignment: '=' expr;
expr: fcall | ident | literal | expr '.' expr;
fcall: fname LPAREN fparams RPAREN;
fname: IDENTIFIER;
fparams: fparam? (',' fparam)*;
fparam: expr;
literal: StringLiteral;
ident : IDENTIFIER;
WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines
LPAREN : '(';
RPAREN : ')';
IDENTIFIER : VALID_ID_START VALID_ID_CHAR*;
fragment VALID_ID_START : ('a' .. 'z') | ('A' .. 'Z') | '_';
fragment VALID_ID_CHAR : VALID_ID_START | ('0' .. '9');
// from https://stackoverflow.com/a/24559773
StringLiteral : UnterminatedStringLiteral '"';
UnterminatedStringLiteral : '"' (~["\\\r\n] | '\\' (. | EOF))*;
// from https://stackoverflow.com/a/23414078
COMMENT: '/*' .*? '*/' -> skip;
LINE_COMMENT: '//' ~[\r\n]* -> skip;
