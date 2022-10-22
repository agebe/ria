// Parser rules start with a lower case letter
// Lexer rules start with a capital letter
grammar Script;
@header {
package org.rescript.antlr;
}

// script can either be a list of statements or a single expression (no need for semicolon in this case)
script
  : header ( stmt* | expr )
  ;

header
  : ( importStmt | functionAlias )*
  ;

importStmt
  : IMPORT STATIC? importType SEMI
  ;

importType
  : Identifier ( '.' Identifier )* ( '.' '*' )?
  ;

functionAlias
  : 'alias' ident dottedIdent SEMI
  ;

dottedIdent
  : Identifier ( '.' Identifier )*
  ;

// https://docs.oracle.com/javase/specs/jls/se6/html/statements.html
// TODO for each statement
// TODO do while statement
// TODO instanceof operator
// TODO bit operators?
// TODO other assign operator e.g. +=, -= etc.
// TODO arrays
// TODO lambda
// TODO script dependencies
// TODO character literal
// TODO text blocks
// TODO switch statement?

// https://docs.oracle.com/javase/tutorial/java/nutsandbolts/expressions.html
// https://github.com/antlr/grammars-v4
// make sure ifStmt comes before ifElseStmt to take care of the dangling else problem
// https://stackoverflow.com/a/70109996

stmt
  : emptyStmt
  | exprStmt
  | vardefStmt
  | returnStmt
  | block
  | ifStmt
  | ifElseStmt
  | whileStmt
  | forStmt
  | breakStmt
  | continueStmt
//  | forEachStmt
  | functionDefinition
  ;
emptyStmt: SEMI;
exprStmt: expr SEMI;
vardefStmt: 'var' ident assignment? ( ',' ident assignment? )* SEMI;
// TODO add support for multiple return expressions?
returnStmt: 'return' expr? SEMI;
block : '{' stmt* '}';
ifStmt: IF LPAREN expr RPAREN stmt;
ifElseStmt: IF LPAREN expr RPAREN stmt ELSE stmt;
whileStmt: WHILE LPAREN expr RPAREN stmt;
forStmt: FOR LPAREN forInit forTerm forInc RPAREN stmt;
forInit: vardefStmt | emptyStmt | assignmentOp ( ',' assignmentOp )* SEMI;
forTerm: expr? SEMI;
forInc: expr? ( ',' expr )*;
//forEarchStmt : FOR LPAREN 'var' ident COLON expr RPARENT stmt;
breakStmt: 'break' SEMI;
continueStmt: 'continue' SEMI;
 
functionDefinition: 'function' fcall block;

assignment: ASSIGN expr;

expr
// do operators first, order by precedence
// https://introcs.cs.princeton.edu/java/11precedence/
  : LPAREN expr RPAREN
  | ident DOT ident ( DOT ident )*
  | expr DOT expr
  | expr ( INC | DEC )
  | ( ADD | SUB | BANG | TILDE | INC | DEC ) expr
  | ccall
  | expr ( MUL | DIV | MOD ) expr
  | expr ( ADD | SUB ) expr
  | expr ( GT | LT | GE | LE ) expr
  | 'typeof' expr
  | expr ( EQUAL | NOTEQUAL) expr
  | expr AND expr
  | expr OR expr
  | expr '?' expr ':' expr
  | assignmentOp
// other expressions below
  | fcall
  | literal
  | ident
  ;

ccall : NEW cname fparams;
cname : Identifier (DOT Identifier)*;
assignmentOp: ident assignment;
fcall: fname fparams;
fname: Identifier;
fparams: LPAREN fparam? (COMMA fparam)* RPAREN;
fparam: expr;

literal
  : strLiteral
  | boolLiteral
  | intLiteral
  | floatLiteral
  | nullLiteral
  ;
strLiteral: StringLiteral ;
boolLiteral: BooleanLiteral ;
intLiteral: IntegerLiteral ;
floatLiteral: FloatingPointLiteral ;
nullLiteral: NullLiteral;
ident : Identifier;
// from https://stackoverflow.com/a/24559773
//StringLiteral : UnterminatedStringLiteral '"';
//UnterminatedStringLiteral : '"' (~["\\\r\n] | '\\' (. | EOF))*;

// from https://github.com/antlr/grammars-v4/blob/master/java/java9/Java9Lexer.g4

// LEXER

// §3.9 Keywords

ABSTRACT : 'abstract';
ASSERT : 'assert';
BOOLEAN : 'boolean';
BREAK : 'break';
BYTE : 'byte';
CASE : 'case';
CATCH : 'catch';
CHAR : 'char';
CLASS : 'class';
CONST : 'const';
CONTINUE : 'continue';
DEFAULT : 'default';
DO : 'do';
DOUBLE : 'double';
ELSE : 'else';
ENUM : 'enum';
EXPORTS : 'exports';
EXTENDS : 'extends';
FINAL : 'final';
FINALLY : 'finally';
FLOAT : 'float';
FOR : 'for';
IF : 'if';
GOTO : 'goto';
IMPLEMENTS : 'implements';
IMPORT : 'import';
INSTANCEOF : 'instanceof';
INT : 'int';
INTERFACE : 'interface';
LONG : 'long';
MODULE : 'module';
NATIVE : 'native';
NEW : 'new';
OPEN : 'open';
OPERNS : 'opens';
PACKAGE : 'package';
PRIVATE : 'private';
PROTECTED : 'protected';
PROVIDES : 'provides';
PUBLIC : 'public';
REQUIRES : 'requires';
RETURN : 'return';
SHORT : 'short';
STATIC : 'static';
STRICTFP : 'strictfp';
SUPER : 'super';
SWITCH : 'switch';
SYNCHRONIZED : 'synchronized';
THIS : 'this';
THROW : 'throw';
THROWS : 'throws';
TO : 'to';
TRANSIENT : 'transient';
TRANSITIVE : 'transitive';
TRY : 'try';
USES : 'uses';
VOID : 'void';
VOLATILE : 'volatile';
WHILE : 'while';
WITH : 'with';
UNDER_SCORE : '_';//Introduced in Java 9

// §3.10.1 Integer Literals

IntegerLiteral
	:	DecimalIntegerLiteral
	|	HexIntegerLiteral
	|	OctalIntegerLiteral
	|	BinaryIntegerLiteral
	;

fragment
DecimalIntegerLiteral
	:	DecimalNumeral IntegerTypeSuffix?
	;

fragment
HexIntegerLiteral
	:	HexNumeral IntegerTypeSuffix?
	;

fragment
OctalIntegerLiteral
	:	OctalNumeral IntegerTypeSuffix?
	;

fragment
BinaryIntegerLiteral
	:	BinaryNumeral IntegerTypeSuffix?
	;

fragment
IntegerTypeSuffix
	:	[lL]
	;

fragment
DecimalNumeral
	:	'0'
	|	NonZeroDigit (Digits? | Underscores Digits)
	;

fragment
Digits
	:	Digit (DigitsAndUnderscores? Digit)?
	;

fragment
Digit
	:	'0'
	|	NonZeroDigit
	;

fragment
NonZeroDigit
	:	[1-9]
	;

fragment
DigitsAndUnderscores
	:	DigitOrUnderscore+
	;

fragment
DigitOrUnderscore
	:	Digit
	|	'_'
	;

fragment
Underscores
	:	'_'+
	;

fragment
HexNumeral
	:	'0' [xX] HexDigits
	;

fragment
HexDigits
	:	HexDigit (HexDigitsAndUnderscores? HexDigit)?
	;

fragment
HexDigit
	:	[0-9a-fA-F]
	;

fragment
HexDigitsAndUnderscores
	:	HexDigitOrUnderscore+
	;

fragment
HexDigitOrUnderscore
	:	HexDigit
	|	'_'
	;

fragment
OctalNumeral
	:	'0' Underscores? OctalDigits
	;

fragment
OctalDigits
	:	OctalDigit (OctalDigitsAndUnderscores? OctalDigit)?
	;

fragment
OctalDigit
	:	[0-7]
	;

fragment
OctalDigitsAndUnderscores
	:	OctalDigitOrUnderscore+
	;

fragment
OctalDigitOrUnderscore
	:	OctalDigit
	|	'_'
	;

fragment
BinaryNumeral
	:	'0' [bB] BinaryDigits
	;

fragment
BinaryDigits
	:	BinaryDigit (BinaryDigitsAndUnderscores? BinaryDigit)?
	;

fragment
BinaryDigit
	:	[01]
	;

fragment
BinaryDigitsAndUnderscores
	:	BinaryDigitOrUnderscore+
	;

fragment
BinaryDigitOrUnderscore
	:	BinaryDigit
	|	'_'
	;

// §3.10.2 Floating-Point Literals

FloatingPointLiteral
	:	DecimalFloatingPointLiteral
	|	HexadecimalFloatingPointLiteral
	;

fragment
DecimalFloatingPointLiteral
	:	Digits '.' Digits? ExponentPart? FloatTypeSuffix?
	|	'.' Digits ExponentPart? FloatTypeSuffix?
	|	Digits ExponentPart FloatTypeSuffix?
	|	Digits FloatTypeSuffix
	;

fragment
ExponentPart
	:	ExponentIndicator SignedInteger
	;

fragment
ExponentIndicator
	:	[eE]
	;

fragment
SignedInteger
	:	Sign? Digits
	;

fragment
Sign
	:	[+-]
	;

fragment
FloatTypeSuffix
	:	[fFdD]
	;

fragment
HexadecimalFloatingPointLiteral
	:	HexSignificand BinaryExponent FloatTypeSuffix?
	;

fragment
HexSignificand
	:	HexNumeral '.'?
	|	'0' [xX] HexDigits? '.' HexDigits
	;

fragment
BinaryExponent
	:	BinaryExponentIndicator SignedInteger
	;

fragment
BinaryExponentIndicator
	:	[pP]
	;

// §3.10.3 Boolean Literals

BooleanLiteral
	:	'true'
	|	'false'
	;

// §3.10.4 Character Literals

CharacterLiteral
	:	'\'' SingleCharacter '\''
	|	'\'' EscapeSequence '\''
	;

fragment
SingleCharacter
	:	~['\\\r\n]
	;
// §3.10.5 String Literals
StringLiteral
	:	'"' StringCharacters? '"'
	;
fragment
StringCharacters
	:	StringCharacter+
	;
fragment
StringCharacter
	:	~["\\\r\n]
	|	EscapeSequence
	;
// §3.10.6 Escape Sequences for Character and String Literals
fragment
EscapeSequence
	:	'\\' [btnfr"'\\]
	|	OctalEscape
    |   UnicodeEscape // This is not in the spec but prevents having to preprocess the input
	;

fragment
OctalEscape
	:	'\\' OctalDigit
	|	'\\' OctalDigit OctalDigit
	|	'\\' ZeroToThree OctalDigit OctalDigit
	;

fragment
ZeroToThree
	:	[0-3]
	;

// This is not in the spec but prevents having to preprocess the input
fragment
UnicodeEscape
    :   '\\' 'u'+ HexDigit HexDigit HexDigit HexDigit
    ;

// §3.10.7 The Null Literal

NullLiteral
	:	'null'
	;

// §3.11 Separators

LPAREN : '(';
RPAREN : ')';
LBRACE : '{';
RBRACE : '}';
LBRACK : '[';
RBRACK : ']';
SEMI : ';';
COMMA : ',';
DOT : '.';
ELLIPSIS : '...';
AT : '@';
COLONCOLON : '::';

// §3.12 Operators

ASSIGN : '=';
GT : '>';
LT : '<';
BANG : '!';
TILDE : '~';
QUESTION : '?';
COLON : ':';
ARROW : '->';
EQUAL : '==';
LE : '<=';
GE : '>=';
NOTEQUAL : '!=';
AND : '&&';
OR : '||';
INC : '++';
DEC : '--';
ADD : '+';
SUB : '-';
MUL : '*';
DIV : '/';
BITAND : '&';
BITOR : '|';
CARET : '^';
MOD : '%';
//LSHIFT : '<<';
//RSHIFT : '>>';
//URSHIFT : '>>>';

ADD_ASSIGN : '+=';
SUB_ASSIGN : '-=';
MUL_ASSIGN : '*=';
DIV_ASSIGN : '/=';
AND_ASSIGN : '&=';
OR_ASSIGN : '|=';
XOR_ASSIGN : '^=';
MOD_ASSIGN : '%=';
LSHIFT_ASSIGN : '<<=';
RSHIFT_ASSIGN : '>>=';
URSHIFT_ASSIGN : '>>>=';

// §3.8 Identifiers (must appear after all keywords in the grammar)

Identifier : JavaLetter JavaLetterOrDigit*;
fragment JavaLetter : [a-zA-Z$_];
fragment JavaLetterOrDigit : [a-zA-Z0-9$_];

//Identifier
//	:	JavaLetter JavaLetterOrDigit*
//	;
//
//fragment
//JavaLetter
//	:	[a-zA-Z$_] // these are the "java letters" below 0x7F
//	|	// covers all characters above 0x7F which are not a surrogate
//		~[\u0000-\u007F\uD800-\uDBFF]
//		{ Check1() }?
//	|	// covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
//		[\uD800-\uDBFF] [\uDC00-\uDFFF]
//		{ Check2() }?
//	;
//
//fragment
//JavaLetterOrDigit
//	:	[a-zA-Z0-9$_] // these are the "java letters or digits" below 0x7F
//	|	// covers all characters above 0x7F which are not a surrogate
//		~[\u0000-\u007F\uD800-\uDBFF]
//		{ Check3() }?
//	|	// covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
//		[\uD800-\uDBFF] [\uDC00-\uDFFF]
//		{ Check4() }?
//	;

//
// Whitespace and comments
//

WS  :  [ \t\r\n\u000C]+ -> skip
    ;

COMMENT
    :   '/*' .*? '*/' -> channel(HIDDEN)
    ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> channel(HIDDEN)
    ;

// from https://stackoverflow.com/a/23414078
//WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines
//COMMENT: '/*' .*? '*/' -> skip;
//LINE_COMMENT: '//' ~[\r\n]* -> skip;
    