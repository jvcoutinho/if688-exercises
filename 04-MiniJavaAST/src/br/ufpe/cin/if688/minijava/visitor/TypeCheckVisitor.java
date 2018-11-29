package br.ufpe.cin.if688.minijava.visitor;

import br.ufpe.cin.if688.minijava.ast.*;
import br.ufpe.cin.if688.minijava.symboltable.Class;
import br.ufpe.cin.if688.minijava.symboltable.Method;
import br.ufpe.cin.if688.minijava.symboltable.SymbolTable;

public class TypeCheckVisitor implements IVisitor<Type> {

	private SymbolTable symbolTable;
	private Class currClass;
	private Method currMethod;
	private GetValueVisitor valueVisitor;

	public TypeCheckVisitor(SymbolTable st) {
		symbolTable = st;
		valueVisitor =  new GetValueVisitor();
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
			error("Tipo de retorno (" + returnType.accept(valueVisitor) + ") diferente de declaração (" + symbolTable.getMethodType(n.i.s, currClass.getId()).accept(valueVisitor) + ")");
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
			error("Expressão do if (" +  n.e.accept(valueVisitor) + ") não é booleana");

		n.s1.accept(this);
		n.s2.accept(this);
		return null;
	}

	// Exp e;
	// Statement s;
	public Type visit(While n) {
		if(!(n.e.accept(this) instanceof BooleanType))
			error("Expressão do while (" +  n.e.accept(valueVisitor) + ") não é booleana");

		n.s.accept(this);
		return null;
	}

	// Exp e;
	public Type visit(Print n) {
		return null;
	}

	// Identifier i;
	// Exp e;
	public Type visit(Assign n) {
		Type variableType = symbolTable.getVarType(currMethod, currClass, n.i.s);
		if(!symbolTable.compareTypes(variableType, n.e.accept(this)))
			error("Atribuição (" + n.accept(valueVisitor) + ") com tipos incompatíveis (" + variableType.accept(valueVisitor) + " = "
					+ n.e.accept(this).accept(valueVisitor) + ")");

		return null;
	}

	// Identifier i;
	// Exp e1,e2;
	public Type visit(ArrayAssign n) {

		// Checking the index.
		if(!(n.e1.accept(this) instanceof IntegerType))
			error("Index inválido do array " + n.i.s);

		// Checking the assignment, considering only ints for MiniJava.
		if(!(n.e2.accept(this) instanceof IntegerType))
			error("Atribuição (" + n.accept(valueVisitor) + ") com tipos incompatíveis (int = " + n.e2.accept(this).accept(valueVisitor) + ")");

		return null;
	}

	// Exp e1,e2;
	public Type visit(And n) {
		if(n.e1.accept(this) instanceof BooleanType && n.e2.accept(this) instanceof BooleanType)
			return new BooleanType();

		error("Expressão do and (" + n.accept(valueVisitor) + ") não é booleana");
		return null;
	}

	// Exp e1,e2;
	public Type visit(LessThan n) {
		if(n.e1.accept(this) instanceof IntegerType && n.e2.accept(this) instanceof IntegerType)
			return new BooleanType();

		error("O par da relação menor-ou-igual (" + n.accept(valueVisitor) + ") não é (inteiro, inteiro)");
		return null;
	}

	// Exp e1,e2;
	public Type visit(Plus n) {
		if(!(n.e1.accept(this) instanceof IntegerType && n.e2.accept(this) instanceof IntegerType))
			error("Função soma (" + n.accept(valueVisitor) + ") com operandos não inteiros");

		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(Minus n) {
		if(!(n.e1.accept(this) instanceof IntegerType && n.e2.accept(this) instanceof IntegerType))
			error("Função subtração (" + n.accept(valueVisitor) + ") com operandos não inteiros");

		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(Times n) {
		if(!(n.e1.accept(this) instanceof IntegerType && n.e2.accept(this) instanceof IntegerType))
			error("Função multiplicação (" + n.accept(valueVisitor) + ") com operandos não inteiros");

		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(ArrayLookup n) {

		// Checking the array.
		if(!(n.e1.accept(this) instanceof IntArrayType))
			error("Tentativa de acesso a uma variável não-array (" + n.accept(valueVisitor) + ")");

		// Checking the index.
		if(!(n.e2.accept(this) instanceof IntegerType))
			error("Index inválido do array " + n.e1.accept(valueVisitor));

		// Considering only ints.
		return new IntegerType();
	}

	// Exp e;
	public Type visit(ArrayLength n) {

		// Checking the array.
		if(!(n.e.accept(this) instanceof IntArrayType))
			error("Tentativa de acesso a uma variável não-array (" + n.accept(valueVisitor) + ")");

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
			error("Chamada do método (" + n.accept(valueVisitor) + ") não corresponde à sua declaração,");
		for (int i = 0; i < n.el.size(); i++) {
			if(!symbolTable.compareTypes(method.getParamAt(i).type(), n.el.elementAt(i).accept(this)))
				error("Chamada do método (" + n.accept(valueVisitor) + ") não corresponde à sua declaração,");
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
			error("Criação de objeto array inválida (" + n.accept(valueVisitor) + ")");

		return new IntArrayType();
	}

	// Identifier i;
	public Type visit(NewObject n) {
		if(symbolTable.containsClass(n.i.s))
			return symbolTable.getClass(n.i.s).type();
		else
			error("Criação de objeto de tipo inexistente (" + n.accept(valueVisitor) + ")");
		return new IdentifierType(n.i.s);
	}

	// Exp e;
	public Type visit(Not n) {
		if(n.e.accept(this) instanceof BooleanType)
			return new BooleanType();

		error("Expressão do not (" + n.accept(valueVisitor) + ") não é booleana");
		return null;
	}

	// String s;
	public Type visit(Identifier n) {
		return null;
	}

	private void error(String message) {
		System.err.println(message + " no método " + currMethod.getId() + " da classe " + currClass.getId() + ".");
	}
}
