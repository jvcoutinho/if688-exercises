package br.ufpe.cin.if688.minijava.main;

import br.ufpe.cin.if688.minijava.ast.*;
import br.ufpe.cin.if688.minijava.visitor.PrettyPrintVisitor;

public class Main {

	public static void main(String[] args) {
		MainClass main = new MainClass(
				new Identifier("Teste"),
				new Identifier("Testando"),
				new Print(new IntegerLiteral(2))
		);

		VarDeclList vdl1 = new VarDeclList();
		vdl1.addElement(new VarDecl(
				new BooleanType(),
				new Identifier("flag")
		));
		vdl1.addElement(new VarDecl(
				new IntegerType(),
				new Identifier("num")
		));

		FormalList fl = new FormalList();
		fl.addElement(new Formal(
				new IntegerType(),
				new Identifier("x")
		));
		fl.addElement(new Formal(
				new IntegerType(),
				new Identifier("y")
		));

		VarDeclList vdlm1 = new VarDeclList();
		vdlm1.addElement(new VarDecl(
				new BooleanType(),
				new Identifier("run")
		));
		vdlm1.addElement(new VarDecl(
				new IntegerType(),
				new Identifier("count")
		));

		MethodDeclList mdl = new MethodDeclList();
		mdl.addElement( new MethodDecl(
				new IntegerType(),
				new Identifier("func"),
				fl,
				vdlm1,
				new StatementList(),
				new IdentifierExp("run")
		));

		mdl.addElement( new MethodDecl(
				new IntegerType(),
				new Identifier("func2"),
				new FormalList(),
				new VarDeclList(),
				new StatementList(),
				new IntegerLiteral(0)
		));

		ClassDeclSimple A = new ClassDeclSimple(
				new Identifier("A"), vdl1, mdl
		);

		ClassDeclExtends B = new ClassDeclExtends(
				new Identifier("B"), new Identifier("A"),
				new VarDeclList(), new MethodDeclList()
		);

		VarDeclList vdl2 = new VarDeclList();
		vdl2.addElement(new VarDecl(
				new IdentifierType("A"),
				new Identifier("obj")
		));
		ClassDeclSimple C = new ClassDeclSimple(
				new Identifier("C"), vdl2, new MethodDeclList()
		);

		ClassDeclList cdl = new ClassDeclList();
		cdl.addElement(A);
		cdl.addElement(B);
		cdl.addElement(C);

		Program p = new Program(main, cdl);
		
		PrettyPrintVisitor ppv = new PrettyPrintVisitor();
		ppv.visit(p);
	}

}
