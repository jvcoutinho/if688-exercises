grammar MiniJava;

/*******************
* GRAMATICAL RULES (CONTEXT-FREE GRAMMAR).
********************/
program:
    mainClass otherClass* EOF;

mainClass:
    'class' identifier '{' 'public' 'static' 'void' 'main' '(' 'String' '[' ']' identifier ')' '{' statement '}' '}';

otherClass:
    'class' identifier ('extends' identifier)? '{' field* method* '}';

method:
    'public' type identifier '(' (parameter)* ')' '{' field* statement* 'return' expression ';' '}';

field:
    type identifier ';';

parameter:
    type identifier | type identifier ',';

identifier:
    Identifier;

type: 'int' | 'boolean' | 'int' '[' ']' | identifier;

/************
* Statements.
*************/
statement:
        '{' statement* '}' #Block
    |   'if' '(' expression ')' statement 'else' statement #ConditionalStatement
    |   'while' '(' expression ')' statement #LoopStatement
    |    'System' '.' 'out' '.' 'println' '(' expression ')' ';' #PrintStatement
    |   identifier '=' expression ';' #AssignmentStatement
    |   identifier '[' expression ']' '=' expression ';' #ArrayAssignmentStatement;

/************
* Expressions.
*************/
expression:
        expression BinaryOperators expression #BinaryOperationExpression
    |   expression '[' expression ']' #ArrayLookupExpression
    |   expression '.' 'length' #ArrayLengthExpression
    |   expression '.' identifier '(' argument* ')' #CallExpression
    |   'true' #TrueExpression
    |   'false' #FalseExpression
    |   Integer #IntegerExpression
    |   identifier #IdentifierExpression
    |   'new' 'int' '[' expression ']' #NewArrayExpression
    |   'new' identifier '(' ')' #NewObjectExpression
    |   'this' #ThisExpression
    |   '!' expression #NegationExpression
    |   '(' expression ')' #ParenthesisExpression;

argument:
    expression | expression ',';

/*******************
* LEXICAL RULES (REGULAR EXPRESSIONS).
********************/
Identifier: [a-zA-Z_][a-zA-Z0-9_]*;
Integer: '0' | [1-9][0-9]*;
BinaryOperators: '<' | '&&' | '-' | '+' | '*';

WS: [ \n\t\r]+ -> skip; // Ignoring whitespaces and line-breaks.
COMMENTS: ( ('//' .*? '\n') | ('/*' (COMMENTS | .)*? '*/') ) -> skip; // Ignoring comments.