package br.ufpe.cin.if688.minijava.visitor;

import br.ufpe.cin.if688.minijava.ast.*;

public class GetValueVisitor implements IVisitor<String> {

    @Override
    public String visit(Program n) {
        return null;
    }

    @Override
    public String visit(MainClass n) {
        return null;
    }

    @Override
    public String visit(ClassDeclSimple n) {
        return null;
    }

    @Override
    public String visit(ClassDeclExtends n) {
        return null;
    }

    @Override
    public String visit(VarDecl n) {
        return null;
    }

    @Override
    public String visit(MethodDecl n) {
        return null;
    }

    @Override
    public String visit(Formal n) {
        return null;
    }

    @Override
    public String visit(IntArrayType n) {
        return "int[]";
    }

    @Override
    public String visit(BooleanType n) {
        return "boolean";
    }

    @Override
    public String visit(IntegerType n) {
        return "int";
    }

    @Override
    public String visit(IdentifierType n) {
        return n.s;
    }

    @Override
    public String visit(Block n) {
        return null;
    }

    @Override
    public String visit(If n) {
        return null;
    }

    @Override
    public String visit(While n) {
        return null;
    }

    @Override
    public String visit(Print n) {
        return null;
    }

    @Override
    public String visit(Assign n) {
        return n.i.accept(this) + " = " + n.e.accept(this);
    }

    @Override
    public String visit(ArrayAssign n) {
        return n.i.accept(this) + "[" + n.e1.accept(this) + "]" + " = " + n.e2.accept(this);
    }

    @Override
    public String visit(And n) {
        return n.e1.accept(this) + " && " + n.e2.accept(this);
    }

    @Override
    public String visit(LessThan n) {
        return n.e1.accept(this) + " < " + n.e2.accept(this);
    }

    @Override
    public String visit(Plus n) {
        return n.e1.accept(this) + " + " + n.e2.accept(this);
    }

    @Override
    public String visit(Minus n) {
        return n.e1.accept(this) + " - " + n.e2.accept(this);
    }

    @Override
    public String visit(Times n) {
        return null;
    }

    @Override
    public String visit(ArrayLookup n) {
        return n.e1.accept(this) + "[" + n.e2.accept(this) + "]";
    }

    @Override
    public String visit(ArrayLength n) {
        return null;
    }

    @Override
    public String visit(Call n) {
        StringBuilder value = new StringBuilder().append(n.i.s).append("(");
        for(int i = 0; i < n.el.size(); i++) {
            value.append(n.el.elementAt(i).accept(this));
            if(i < n.el.size() - 1)
                value.append(", ");
        }
        value.append(")");
        return value.toString();
    }

    @Override
    public String visit(IntegerLiteral n) {
        return String.valueOf(n.i);
    }

    @Override
    public String visit(True n) {
        return "true";
    }

    @Override
    public String visit(False n) {
        return "false";
    }

    @Override
    public String visit(IdentifierExp n) {
        return n.s;
    }

    @Override
    public String visit(This n) {
        return null;
    }

    @Override
    public String visit(NewArray n) {
        return "new int[" + n.e.accept(this) + "]";
    }

    @Override
    public String visit(NewObject n) {
        return "new " + n.i.s + "()";
    }

    @Override
    public String visit(Not n) {
        return "!" + n.e.accept(this);
    }

    @Override
    public String visit(Identifier n) {
        return n.s;
    }
}
