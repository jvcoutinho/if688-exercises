package br.ufpe.cin.if688.minijava.parser;

import br.ufpe.cin.if688.minijava.ast.*;
import br.ufpe.cin.if688.minijava.generated.MiniJavaParser;
import br.ufpe.cin.if688.minijava.generated.MiniJavaVisitor;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class ASTVisitor implements MiniJavaVisitor<Object> {

    @Override
    public Program visitProgram(MiniJavaParser.ProgramContext ctx) {
        MainClass mainClass = (MainClass) ctx.mainClass().accept(this);

        ClassDeclList classDeclList = new ClassDeclList();
        for (MiniJavaParser.OtherClassContext otherClass : ctx.otherClass())
            classDeclList.addElement((ClassDecl) otherClass.accept(this));

        return new Program(mainClass, classDeclList);
    }

    @Override
    public Object visitMainClass(MiniJavaParser.MainClassContext ctx) {
        Identifier i1 = (Identifier) ctx.identifier(0).accept(this);
        Identifier i2 = (Identifier) ctx.identifier(1).accept(this);
        Statement s = (Statement) ctx.statement().accept(this);

        return new MainClass(i1, i2, s);
    }

    @Override
    public Object visitOtherClass(MiniJavaParser.OtherClassContext ctx) {
        if(ctx.identifier().size() == 2) // 2 identifiers ==> Extends.
            return visitClassDeclExtends(ctx);
        else
            return visitClassDeclSimple(ctx);
    }

    private Object visitClassDeclExtends(MiniJavaParser.OtherClassContext ctx) {
        Identifier i = (Identifier) ctx.identifier(0).accept(this);
        Identifier j = (Identifier) ctx.identifier(1).accept(this);

        VarDeclList varDeclList = new VarDeclList();
        for(MiniJavaParser.FieldContext varDecl : ctx.field())
            varDeclList.addElement((VarDecl) varDecl.accept(this));

        MethodDeclList methodDeclList = new MethodDeclList();
        for(MiniJavaParser.MethodContext methodDecl : ctx.method())
            methodDeclList.addElement((MethodDecl) methodDecl.accept(this));

        return new ClassDeclExtends(i, j, varDeclList, methodDeclList);
    }

    @Override
    public Object visitIdentifier(MiniJavaParser.IdentifierContext ctx) {
        return new Identifier(ctx.getText());
    }

    private Object visitClassDeclSimple(MiniJavaParser.OtherClassContext ctx) {
        Identifier i = (Identifier) ctx.identifier(0).accept(this);

        VarDeclList varDeclList = new VarDeclList();
        for(MiniJavaParser.FieldContext varDecl : ctx.field())
            varDeclList.addElement((VarDecl) varDecl.accept(this));

        MethodDeclList methodDeclList = new MethodDeclList();
        for(MiniJavaParser.MethodContext methodDecl : ctx.method())
            methodDeclList.addElement((MethodDecl) methodDecl.accept(this));

        return new ClassDeclSimple(i, varDeclList, methodDeclList);
    }

    @Override
    public Object visitMethod(MiniJavaParser.MethodContext ctx) {
        Type t = (Type) ctx.type().accept(this);
        Identifier i = (Identifier) ctx.identifier().accept(this);

        FormalList formalList = new FormalList();
        for (MiniJavaParser.ParameterContext formal : ctx.parameter())
            formalList.addElement((Formal) formal.accept(this));

        VarDeclList varDeclList = new VarDeclList();
        for(MiniJavaParser.FieldContext varDecl : ctx.field())
            varDeclList.addElement((VarDecl) varDecl.accept(this));

        StatementList statementList = new StatementList();
        for(MiniJavaParser.StatementContext statement : ctx.statement())
            statementList.addElement((Statement) statement.accept(this));

        Exp e = (Exp) ctx.expression().accept(this);

        return new MethodDecl(t, i, formalList, varDeclList, statementList, e);
    }

    @Override
    public Object visitField(MiniJavaParser.FieldContext ctx) {
        Type t = (Type) ctx.type().accept(this);
        Identifier i = (Identifier) ctx.identifier().accept(this);

        return new VarDecl(t, i);
    }

    @Override
    public Object visitParameter(MiniJavaParser.ParameterContext ctx) {
        Type t = (Type) ctx.type().accept(this);
        Identifier i = (Identifier) ctx.identifier().accept(this);

        return new Formal(t, i);
    }

    @Override
    public Object visitType(MiniJavaParser.TypeContext ctx) {
        switch (ctx.getText()) {
            case "boolean":
                return new BooleanType();
            case "int":
                return new IntegerType();
            case "int[]":
                return new IntArrayType();
            default:
                return new IdentifierType(ctx.getText());
        }
    }

    @Override
    public Object visitPrintStatement(MiniJavaParser.PrintStatementContext ctx) {
        Exp e = (Exp) ctx.expression().accept(this);

        return new Print(e);
    }

    @Override
    public Object visitConditionalStatement(MiniJavaParser.ConditionalStatementContext ctx) {
        Exp e = (Exp) ctx.expression().accept(this);
        Statement s1 = (Statement) ctx.statement(0).accept(this);
        Statement s2 = (Statement) ctx.statement(1).accept(this);

        return new If(e, s1, s2);
    }

    @Override
    public Object visitAssignmentStatement(MiniJavaParser.AssignmentStatementContext ctx) {
        Identifier i = (Identifier) ctx.identifier().accept(this);
        Exp e = (Exp) ctx.expression().accept(this);

        return new Assign(i, e);
    }

    @Override
    public Object visitArrayAssignmentStatement(MiniJavaParser.ArrayAssignmentStatementContext ctx) {
        Identifier i = (Identifier) ctx.identifier().accept(this);
        Exp e1 = (Exp) ctx.expression(0).accept(this);
        Exp e2 = (Exp) ctx.expression(1).accept(this);

        return new ArrayAssign(i, e1, e2);
    }

    @Override
    public Object visitBlock(MiniJavaParser.BlockContext ctx) {
        StatementList statementList = new StatementList();
        for(MiniJavaParser.StatementContext statement : ctx.statement())
            statementList.addElement((Statement) statement.accept(this));

        return new Block(statementList);
    }

    @Override
    public Object visitLoopStatement(MiniJavaParser.LoopStatementContext ctx) {
        Exp e = (Exp) ctx.expression().accept(this);
        Statement s = (Statement) ctx.statement().accept(this);

        return new While(e, s);
    }

    @Override
    public Object visitThisExpression(MiniJavaParser.ThisExpressionContext ctx) {
        return new This();
    }

    @Override
    public Object visitArrayLengthExpression(MiniJavaParser.ArrayLengthExpressionContext ctx) {
        Exp e = (Exp) ctx.expression().accept(this);

        return new ArrayLength(e);
    }

    @Override
    public Object visitArrayLookupExpression(MiniJavaParser.ArrayLookupExpressionContext ctx) {
        Exp e1 = (Exp) ctx.expression(0).accept(this);
        Exp e2 = (Exp) ctx.expression(1).accept(this);

        return new ArrayLookup(e1, e2);
    }

    @Override
    public Object visitNewArrayExpression(MiniJavaParser.NewArrayExpressionContext ctx) {
        Exp e = (Exp) ctx.expression().accept(this);

        return new NewArray(e);
    }

    @Override
    public Object visitCallExpression(MiniJavaParser.CallExpressionContext ctx) {
        Exp e = (Exp) ctx.expression().accept(this);
        Identifier i = (Identifier) ctx.identifier().accept(this);

        ExpList expList = new ExpList();
        for(MiniJavaParser.ArgumentContext exp : ctx.argument())
            expList.addElement((Exp) exp.accept(this));

        return new Call(e, i, expList);
    }

    @Override
    public Object visitNewObjectExpression(MiniJavaParser.NewObjectExpressionContext ctx) {
        Identifier i = (Identifier) ctx.identifier().accept(this);

        return new NewObject(i);
    }

    @Override
    public Object visitIdentifierExpression(MiniJavaParser.IdentifierExpressionContext ctx) {
        return new IdentifierExp(ctx.getText());
    }

    @Override
    public Object visitTrueExpression(MiniJavaParser.TrueExpressionContext ctx) {
        return new True();
    }

    @Override
    public Object visitBinaryOperationExpression(MiniJavaParser.BinaryOperationExpressionContext ctx) {
        Exp e1 = (Exp) ctx.expression(0).accept(this);
        Exp e2 = (Exp) ctx.expression(1).accept(this);

        switch (ctx.BinaryOperators().getText()) {
            case "<":
                return new LessThan(e1, e2);
            case "&&":
                return new And(e1, e2);
            case "+":
                return new Plus(e1, e2);
            case "-":
                return new Minus(e1, e2);
            default:
                return new Times(e1, e2);
        }
    }

    @Override
    public Object visitIntegerExpression(MiniJavaParser.IntegerExpressionContext ctx) {
        return new IntegerLiteral(Integer.parseInt(ctx.getText()));
    }

    @Override
    public Object visitNegationExpression(MiniJavaParser.NegationExpressionContext ctx) {
        Exp e = (Exp) ctx.expression().accept(this);

        return new Not(e);
    }

    @Override
    public Object visitFalseExpression(MiniJavaParser.FalseExpressionContext ctx) {
        return new False();
    }

    @Override
    public Object visitParenthesisExpression(MiniJavaParser.ParenthesisExpressionContext ctx) {
        return ctx.expression().accept(this);
    }

    @Override
    public Object visitArgument(MiniJavaParser.ArgumentContext ctx) {
        return ctx.expression().accept(this);
    }

    @Override
    public Object visit(ParseTree parseTree) {
        return null;
    }

    @Override
    public Object visitChildren(RuleNode ruleNode) {
        return null;
    }

    @Override
    public Object visitTerminal(TerminalNode terminalNode) {
        return null;
    }

    @Override
    public Object visitErrorNode(ErrorNode errorNode) {
        return null;
    }


}
