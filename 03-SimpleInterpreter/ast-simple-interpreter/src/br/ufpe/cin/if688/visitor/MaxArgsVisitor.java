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

public class MaxArgsVisitor implements IVisitor<Integer> {

	@Override
	public Integer visit(Stm s) {
		return s.accept(this);
	}

	@Override
	public Integer visit(AssignStm s) {
		return s.getExp().accept(this);
	}

	@Override
	public Integer visit(CompoundStm s) {
		int esq = s.getStm1().accept(this);
		int dir = s.getStm2().accept(this);
		return Math.max(esq, dir);
	}

	@Override
	public Integer visit(PrintStm s) {
		return Math.max(countParameters(s.getExps()), s.getExps().accept(this));
	}

	@Override
	public Integer visit(Exp e) {
		return e.accept(this);
	}

	@Override
	public Integer visit(EseqExp e) {
		return Math.max(e.getStm().accept(this), e.getExp().accept(this));
	}

	@Override
	public Integer visit(IdExp e) {
		return 0;
	}

	@Override
	public Integer visit(NumExp e) {
		return 0;
	}

	@Override
	public Integer visit(OpExp e) {
		return Math.max(e.getLeft().accept(this), e.getRight().accept(this));
	}

	@Override
	public Integer visit(ExpList el) {
		return el.accept(this);
	}

	@Override
	public Integer visit(PairExpList el) {
		return Math.max(el.getHead().accept(this), el.getTail().accept(this));
	}

	@Override
	public Integer visit(LastExpList el) {
		return el.getHead().accept(this);
	}

	private int countParameters(ExpList el) {
		if(el instanceof LastExpList)
			return 1;
		else
			return 1 + countParameters(((PairExpList) el).getTail());
	}

}
