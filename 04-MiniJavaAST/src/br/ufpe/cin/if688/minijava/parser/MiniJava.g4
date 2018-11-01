grammar MiniJava;

/*******************
* GRAMATICAL RULES (CONTEXT-FREE GRAMMAR).
********************/
program:
    mainClass otherClass* EOF;

mainClass:
    'class' Identifier '{' 'public' 'static' 'Void' 'main' '(' 'String' '[' ']' Identifier ')' '{' statement '}' '}';

otherClass:
    'class' Identifier ('extends' Identifier)? '{' field* method* '}';

method:
    'public' type Identifier '(' (parameter)* ')' '{' field* statement* 'return' expression ';' '}';

field:
    type Identifier ';';

parameter:
    type Identifier | type Identifier ',';

type: 'int' | 'boolean' | 'int' '[' ']' | Identifier;

/************
* Statements.
*************/
statement:
    printStatement |
    conditionalStatement |
    assignmentStatement |
    arrayAssignmentStatement |
    block |
    loopStatement;

    printStatement:
        'System' '.' 'out' '.' 'println' '(' expression ')' ';';

    conditionalStatement:
        'if' '(' expression ')' statement 'else' statement;

    assignmentStatement:
        Identifier '=' expression ';';

    arrayAssignmentStatement:
        Identifier '[' expression '] = ' expression ';';

    block:
        '{' statement* '}';

    loopStatement:
        'while' '(' expression ')' statement;


/************
* Expressions.
*************/
expression:
    booleanExpression |
    arithmeticExpression |
    expression '.' 'length' | // ArrayLength.
    expression '[' expression ']' | // ArrayLookup.
    expression '.' Identifier '(' argument* ')' | // Call.
    'new' 'int' '[' expression ']' | // NewArray.
    'new' Identifier '(' ')' | // NewObject.
    'this' |
    Identifier;

    booleanExpression:
        '(' expression BinaryOperators expression ')' | '!' expression | 'true' | 'false';

    arithmeticExpression:
        '(' expression BinaryOperators expression ')' | Integer;

argument:
    expression | expression ',';

/*******************
* LEXICAL RULES (REGULAR EXPRESSIONS).
********************/
Identifier: [a-zA-Z_][a-zA-Z0-9_]*;
Integer: '0' | [1-9][0-9]*;
BinaryOperators: '<' | '&&' | '-' | '+' | '*';

WS: [ \n\t\r]+ -> skip; // Ignoring whitespaces and line-breaks.
COMMENTS: ( ('//' .*? '\n') | ('/*' .*? '*/') ) -> skip; // Ignoring comments.