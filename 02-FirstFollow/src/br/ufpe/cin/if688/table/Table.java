package br.ufpe.cin.if688.table;


import br.ufpe.cin.if688.parsing.analysis.*;
import br.ufpe.cin.if688.parsing.grammar.*;
import java.util.*;


public final class Table {
    private Table() {    }

    public static Map<LL1Key, List<GeneralSymbol>> createTable(Grammar g) throws NotLL1Exception {
        if (g == null) throw new NullPointerException();

        Map<Nonterminal, Set<GeneralSymbol>> first =
                SetGenerator.getFirst(g);
        Map<Nonterminal, Set<GeneralSymbol>> follow =
                SetGenerator.getFollow(g, first);

        Map<LL1Key, List<GeneralSymbol>> parsingTable =
                new HashMap<LL1Key, List<GeneralSymbol>>();

        Iterator<Production> rules = g.iterator();
        while(rules.hasNext()) {
        	
        	// Regra A -> X e FIRST(X)
        	Production rule = rules.next();
        	Set<GeneralSymbol> firstSet = SetGenerator.computeFirst(first, rule.getProduction(), 0);
        	       	
        	// Para todo a em FIRST(X), adicione A -> X em M[A, a]
        	Iterator<GeneralSymbol> symbols = firstSet.iterator();
        	while(symbols.hasNext()) {
        		GeneralSymbol nextSymbol = symbols.next();
        		
        		if(!nextSymbol.equals(SpecialSymbol.EPSILON)) 
	        		parsingTable.put(new LL1Key(rule.getNonterminal(), nextSymbol), rule.getProduction());
        		
        		// Se a cadeia vazia está em FIRST(X), então adicione A -> X em M[A, b] para cada b em FOLLOW(A)
        		else {
        			Iterator<GeneralSymbol> followSymbols = follow.get(rule.getNonterminal()).iterator();
        			while(followSymbols.hasNext()) {
        				GeneralSymbol followNextSymbol = followSymbols.next();
        				parsingTable.put(new LL1Key(rule.getNonterminal(), followNextSymbol), rule.getProduction());
        			}
        		}
        		
        	}
        }

        // Prints para o iudex.
        System.out.print(g.toString());
        System.out.println(first.toString());
        System.out.println(follow.toString());
        System.out.println(parsingTable.toString());
        
        return parsingTable;
    }
}
