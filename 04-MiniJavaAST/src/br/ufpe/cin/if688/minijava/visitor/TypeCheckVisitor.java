package br.ufpe.cin.if688.minijava.visitor;

import br.ufpe.cin.if688.minijava.ast.*;
import br.ufpe.cin.if688.minijava.symboltable.Class;
import br.ufpe.cin.if688.minijava.symboltable.Method;
import br.ufpe.cin.if688.minijava.symboltable.SymbolTable;

public class TypeCheckVisitor implements IVisitor<Type> {

	private SymbolTable symbolTable;
	private Class currClass;
	private Method currMethod;

	public TypeCheckVisitor(SymbolTable st) {
		symbolTable = st;
	}

	// MainClass m;
	// ClassDeclList cl;
	public Type visit(Program n) {
		n.m.accept(this);
		for (int i = 0; i < n.cl.size(); i++) {
			n.cl.elementAt(i).accept(this);
		}
		return null;
	}

	// Identifier i1,i2;
	// Statement s;
	public Type visit(MainClass n) {
		currClass = symbolTable.getClass(n.i1.s);

		n.s.accept(this);
		return null;
	}

	// Identifier i;
	// VarDeclList vl;
	// MethodDeclList ml;
	public Type visit(ClassDeclSimple n) {
		currClass = symbolTable.getClass(n.i.s);

		for (int i = 0; i < n.ml.size(); i++) {
			n.ml.elementAt(i).accept(this);
		}
		return null;
	}

	// Identifier i;
	// Identifier j;
	// VarDeclList vl;
	// MethodDeclList ml;
	public Type visit(ClassDeclExtends n) {
		currClass = symbolTable.getClass(n.i.s);

		for (int i = 0; i < n.ml.size(); i++) {
			n.ml.elementAt(i).accept(this);
		}
		return null;
	}

	// Type t;
	// Identifier i;
	public Type visit(VarDecl n) {
		return null;
	}

	// Type t;
	// Identifier i;
	// FormalList fl;
	// VarDeclList vl;
	// StatementList sl;
	// Exp e;
	public Type visit(MethodDecl n) {
		currMethod = symbolTable.getMethod(n.i.s, currClass.getId());

		for (int i = 0; i < n.sl.size(); i++) {
			n.sl.elementAt(i).accept(this);
		}

		Type returnType = n.e.accept(this);
		if(!symbolTable.compareTypes(returnType, symbolTable.getMethodType(n.i.s, currClass.getId())))
			error("Erro de tipo! Retorno diferente de declaração!");
		return null;
	}

	// Type t;
	// Identifier i;
	public Type visit(Formal n) {
		return null;
	}

	public Type visit(IntArrayType n) {
		return n;
	}

	public Type visit(BooleanType n) {
		return n;
	}

	public Type visit(IntegerType n) {
		return n;
	}

	// String s;
	public Type visit(IdentifierType n) {
		return n;
	}

	// StatementList sl;
	public Type visit(Block n) {
		for (int i = 0; i < n.sl.size(); i++) {
			n.sl.elementAt(i).accept(this);
		}
		return null;
	}

	// Exp e;
	// Statement s1,s2;
	public Type visit(If n) {
		if(!(n.e.accept(this) instanceof BooleanType))
			error("Erro de tipo! Esperava um boolean no If.");

		n.s1.accept(this);
		n.s2.accept(this);
		return null;
	}

	// Exp e;
	// Statement s;
	public Type visit(While n) {
		if(!(n.e.accept(this) instanceof BooleanType))
			error("Erro de tipo! Esperava um boolean no While.");

		n.s.accept(this);
		return null;
	}

	// Exp e;
	public Type visit(Print n) {
		if(!((n.e.accept(this) instanceof IntegerType) || (n.e.accept(this) instanceof BooleanType)))
			error("Esperava um int ou boolean no Print.");
		return null;
	}

	// Identifier i;
	// Exp e;
	public Type visit(Assign n) {
		Type variableType = symbolTable.getVarType(currMethod, currClass, n.i.s);
		if(!symbolTable.compareTypes(variableType, n.e.accept(this)))
			error("Erro de tipo! Atribuição com diferentes tipos em " + n.i.s);

		return null;
	}

	// Identifier i;
	// Exp e1,e2;
	public Type visit(ArrayAssign n) {

		// Checking the index.
		if(!(n.e1.accept(this) instanceof IntegerType))
			error("Erro de tipo! Index não é um inteiro em " + n.i.s);

		// Checking the assignment, considering only ints for MiniJava.
		if(!(n.e2.accept(this) instanceof IntegerType))
			error("Erro de tipo! Atribuição com diferentes tipos em " + n.i.s);

		return null;
	}

	// Exp e1,e2;
	public Type visit(And n) {
		if(n.e1.accept(this) instanceof BooleanType && n.e2.accept(this) instanceof BooleanType)
			return new BooleanType();

		error("Erro de tipo! And com diferentes tipos nos operandos.");
		return null;
	}

	// Exp e1,e2;
	public Type visit(LessThan n) {
		if(n.e1.accept(this) instanceof IntegerType && n.e2.accept(this) instanceof IntegerType)
			return new BooleanType();

		error("Erro de tipo! LessThan com diferentes tipos nos operandos.");
		return null;
	}

	// Exp e1,e2;
	public Type visit(Plus n) {
		if(n.e1.accept(this) instanceof IntegerType && n.e2.accept(this) instanceof IntegerType)
			return new IntegerType();

		error("Erro de tipo! Plus com diferentes tipos nos operandos.");
		return null;
	}

	// Exp e1,e2;
	public Type visit(Minus n) {
		if(n.e1.accept(this) instanceof IntegerType && n.e2.accept(this) instanceof IntegerType)
			return new IntegerType();

		error("Erro de tipo! Minus com diferentes tipos nos operandos.");
		return null;
	}

	// Exp e1,e2;
	public Type visit(Times n) {
		if(n.e1.accept(this) instanceof IntegerType && n.e2.accept(this) instanceof IntegerType)
			return new IntegerType();

		error("Erro de tipo! Times com diferentes tipos nos operandos.");
		return null;
	}

	// Exp e1,e2;
	public Type visit(ArrayLookup n) {

		// Checking the array.
		if(!(n.e1.accept(this) instanceof IntArrayType))
			error("Erro de tipo! Não é um array.");

		// Checking the index.
		if(!(n.e2.accept(this) instanceof IntegerType))
			error("Erro de tipo! Index não é um inteiro.");

		// Considering only ints.
		return new IntegerType();
	}

	// Exp e;
	public Type visit(ArrayLength n) {

		// Checking the array.
		if(!(n.e.accept(this) instanceof IntArrayType))
			error("Erro de tipo! Não é um array.");

		return new IntegerType();
	}

	// Exp e;
	// Identifier i;
	// ExpList el;
	public Type visit(Call n) {

		Method method;

		// Checking which method this call refers.
		Type classType = n.e.accept(this);
		if(symbolTable.compareTypes(classType, currClass.type())) // this
			method = symbolTable.getMethod(n.i.s, currClass.getId());
		else // new Object
			method = symbolTable.getMethod(n.i.s, symbolTable.getClass(((IdentifierType) classType).s).getId());

		// Checking if the argument list matches the declaration.
		if(method.getParams().hasMoreElements() && n.el.size() == 0)
			error("Chamada não bate com declaração.");
		for (int i = 0; i < n.el.size(); i++) {
			if(!symbolTable.compareTypes(method.getParamAt(i).type(), n.el.elementAt(i).accept(this)))
				error("Chamada não bate com declaração.");
		}

		return method.type();
	}

	// int i;
	public Type visit(IntegerLiteral n) {
		return new IntegerType();
	}

	public Type visit(True n) {
		return new BooleanType();
	}

	public Type visit(False n) {
		return new BooleanType();
	}

	// String s;
	public Type visit(IdentifierExp n) {
		return symbolTable.getVarType(currMethod, currClass, n.s);
	}

	public Type visit(This n) {
		return currClass.type();
	}

	// Exp e;
	public Type visit(NewArray n) {

		// Checking the index.
		if(!(n.e.accept(this) instanceof IntegerType))
			error("Erro de tipo! Index não é um inteiro.");

		return new IntArrayType();
	}

	// Identifier i;
	public Type visit(NewObject n) {
		if(symbolTable.containsClass(n.i.s))
			return symbolTable.getClass(n.i.s).type();
		else
			error("Tipo inexistente em new.");
		return null;
	}

	// Exp e;
	public Type visit(Not n) {
		if(n.e.accept(this) instanceof BooleanType)
			return new BooleanType();

		error("Erro de tipo! Not com um não booleano como operando.");
		return null;
	}

	// String s;
	public Type visit(Identifier n) {
		return null;
	}

	private void error(String message) {
		System.err.println(message);
		System.exit(1);
	}
}
