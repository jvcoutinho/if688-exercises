package br.ufpe.cin.if688.visitor;

import br.ufpe.cin.if688.ast.AssignStm;
import br.ufpe.cin.if688.ast.CompoundStm;
import br.ufpe.cin.if688.ast.EseqExp;
import br.ufpe.cin.if688.ast.Exp;
import br.ufpe.cin.if688.ast.ExpList;
import br.ufpe.cin.if688.ast.IdExp;
import br.ufpe.cin.if688.ast.LastExpList;
import br.ufpe.cin.if688.ast.NumExp;
import br.ufpe.cin.if688.ast.OpExp;
import br.ufpe.cin.if688.ast.PairExpList;
import br.ufpe.cin.if688.ast.PrintStm;
import br.ufpe.cin.if688.ast.Stm;
import br.ufpe.cin.if688.symboltable.Table;

public class Interpreter implements IVisitor<Table> {

	//a=8;b=80;a=7;
	// a->7 ==> b->80 ==> a->8 ==> NIL
	private IntAndTableVisitor EvalVisitor;
	private static Table head;

	public Interpreter(Table t) {
		head = new Table(null, 0, t);
		EvalVisitor = new IntAndTableVisitor(head.tail);
	}

	@Override
	public Table visit(Stm s) {
		return s.accept(this);
	}

	@Override
	public Table visit(AssignStm s) {
		Table t = new Table(s.getId(), s.getExp().accept(this).value, null);
		pushTable(t);
		return t;
	}

	@Override
	public Table visit(CompoundStm s) {

		Table t1 = s.getStm1().accept(this);
		Table t2 = s.getStm2().accept(this);

		if(t1 != null) {
			if(t2 != null)
				pushTable(t2);
			pushTable(t1);
			return t1;

		} else if(t2 != null){
			pushTable(t2);
			return t2;
		}

		System.out.println("oi");
		System.out.println(head.toString());
		return null;
	}

	@Override
	public Table visit(PrintStm s) {
		printArguments(s.getExps());
		return null;
	}

	@Override
	public Table visit(Exp e) {
		return e.accept(this);
	}

	@Override
	public Table visit(EseqExp e) {
		e.getStm().accept(this);
		return e.getExp().accept(this);
	}

	@Override
	public Table visit(IdExp e) {
		EvalVisitor.setTable(head.tail);
		return new Table(null, EvalVisitor.visit(e).result, null);
	}

	@Override
	public Table visit(NumExp e) {
		EvalVisitor.setTable(head.tail);
		return new Table(null, EvalVisitor.visit(e).result, null);
	}

	@Override
	public Table visit(OpExp e) {
		EvalVisitor.setTable(head.tail);
		return new Table(null, EvalVisitor.visit(e).result, null);
	}

	@Override
	public Table visit(ExpList el) {
		return null;
	}

	@Override
	public Table visit(PairExpList el) {
		return null;
	}

	@Override
	public Table visit(LastExpList el) {
		return null;
	}

    private void printArguments(ExpList el) {

        if(el instanceof LastExpList)
            System.out.print(((LastExpList) el).getHead().accept(this).value + "\n");

        else {
			System.out.print(((PairExpList) el).getHead().accept(this).value + "\n");
			printArguments(((PairExpList) el).getTail());
		}

    }

	static Table getIdentifier(Table t, String id) {
		if(t == null)
			return null;
		else if(t.id.equals(id))
			return t;
		return getIdentifier(t.tail, id);
	}

	private void pushTable(Table t) {
		t.tail = head.tail;
		head.tail = t;
	}

}
