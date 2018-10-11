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
import br.ufpe.cin.if688.symboltable.IntAndTable;
import br.ufpe.cin.if688.symboltable.Table;

public class IntAndTableVisitor implements IVisitor<IntAndTable> {
	private Table t;

	IntAndTableVisitor(Table t) {
		this.t = t;
	}

	void setTable(Table t) {
		this.t = t;
	}

	@Override
	public IntAndTable visit(Stm s) {
		return s.accept(this);
	}

	@Override
	public IntAndTable visit(AssignStm s) {
		return new IntAndTable(s.getExp().accept(this).result, t);
	}

	@Override
	public IntAndTable visit(CompoundStm s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntAndTable visit(PrintStm s) {

		return null;
	}

	@Override
	public IntAndTable visit(Exp e) {
		return e.accept(this);
	}

	@Override
	public IntAndTable visit(EseqExp e) {
		return null;
	}

	@Override
	public IntAndTable visit(IdExp e) {
		//System.out.println(Interpreter.head.toString());
		Table t = Interpreter.getIdentifier(this.t, e.getId());
		return new IntAndTable(t.value, t);
	}

	@Override
	public IntAndTable visit(NumExp e) {
		return new IntAndTable(e.getNum(), t);
	}

	@Override
	public IntAndTable visit(OpExp e) {

		IntAndTable t1 = e.getLeft().accept(this);
		IntAndTable t2 = e.getRight().accept(this);

		switch(e.getOper()) {
			case 1:
				return new IntAndTable(t1.result + t2.result, t);
			case 2:
				return new IntAndTable(t1.result - t2.result, t);
			case 3:
				return new IntAndTable(t1.result * t2.result, t);
			default:
				return new IntAndTable(t1.result / t2.result, t);
		}
	}

	@Override
	public IntAndTable visit(ExpList el) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntAndTable visit(PairExpList el) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntAndTable visit(LastExpList el) {
		// TODO Auto-generated method stub
		return null;
	}

}
