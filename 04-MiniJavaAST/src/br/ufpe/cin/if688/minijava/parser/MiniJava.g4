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
        'System' '.' 'out' '.' 'println' '(' expression ')' ';' #PrintStatement
    |   'if' '(' expression ')' statement 'else' statement #ConditionalStatement
    |   identifier '=' expression ';' #AssignmentStatement
    |   identifier '[' expression '] = ' expression ';' #ArrayAssignmentStatement
    |   '{' statement* '}' #Block
    |   'while' '(' expression ')' statement #LoopStatement;

/************
* Expressions.
*************/
expression:
        expression BinaryOperators expression #BinaryOperationExpression
    |   '!' expression #NegationExpression
    |   'true' #TrueExpression
    |   'false' #FalseExpression
    |   Integer #IntegerExpression
    |   expression '.' 'length' #ArrayLengthExpression
    |   expression '[' expression ']' #ArrayLookupExpression
    |   expression '.' identifier '(' argument* ')' #CallExpression
    |   'new' 'int' '[' expression ']' #NewArrayExpression
    |   'new' identifier '(' ')' #NewObjectExpression
    |   'this' #ThisExpression
    |   identifier #IdentifierExpression
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