package br.ufpe.cin.if688.parsing.analysis;

import java.util.*;

import br.ufpe.cin.if688.parsing.grammar.*;


public final class SetGenerator {

    public static Map<Nonterminal, Set<GeneralSymbol>> getFirst(Grammar g) {

        if (g == null) throw new NullPointerException("g nao pode ser nula.");

        Map<Nonterminal, Set<GeneralSymbol>> first = initializeNonterminalMapping(g);
        
        boolean hasChanged;
        do {
        	hasChanged = false;
        	
	        Iterator<Production> productions = g.iterator();
	    	while(productions.hasNext()) {
	    		Nonterminal variable = productions.next().getNonterminal();
	    		
	    		Set<GeneralSymbol> firstSet = computeFirst(g, first, variable);
	    		
	    		// Qualquer conjunto modificado muda esse booleano para true.
	    		if(!firstSet.equals(first.get(variable))) 
	    			hasChanged = true;	    			
	    		
	    		first.put(variable, firstSet);       		
	    	}
    	
        } while(hasChanged);

        return first;

    }
    
    // Método para tratar o caso trivial do conjunto FIRST, dado que as chaves do map são apenas não-terminais.
    private static Set<GeneralSymbol> retrieveFirst(Map<Nonterminal, Set<GeneralSymbol>> first, GeneralSymbol symbol) {
    	
    	if(symbol instanceof Terminal || symbol.equals(SpecialSymbol.EPSILON)) {
    		Set<GeneralSymbol> firstSet = new HashSet<GeneralSymbol>();
    		firstSet.add(symbol);
    		return firstSet;
    	} 
    	
    	else 
    		return first.get(symbol);	
    }
    
    public static Set<GeneralSymbol> computeFirst(Grammar g, Map<Nonterminal, Set<GeneralSymbol>> first, Nonterminal variable) {
    	
    	Set<GeneralSymbol> firstSet = new HashSet<GeneralSymbol>();
    			    	
		Iterator<Production> ruleSet = g.iterator();
		while(ruleSet.hasNext()) {
			Production rule = ruleSet.next();			
			if(rule.getNonterminal().equals(variable)) {
				
				if(derivesEmptyString(rule.getProduction()))
		    		firstSet.add(SpecialSymbol.EPSILON);
				
				else 
					firstSet.addAll(computeFirst(first, rule.getProduction(), 0));
				
			}		    			
		}
    	   	
    	return firstSet;
    }
    
    private static boolean derivesEmptyString(List<GeneralSymbol> rule) {
    	if(rule.get(0).equals(SpecialSymbol.EPSILON))
    		return true;
    	return false;
    }
    
    public static Set<GeneralSymbol> computeFirst(Map<Nonterminal, Set<GeneralSymbol>> first, List<GeneralSymbol> rule, int start) {
    	
    	Set<GeneralSymbol> firstSet = new HashSet<GeneralSymbol>();
    	firstSet.addAll(retrieveFirst(first, rule.get(start)));
    	
    	for(int i = start + 1; i < rule.size() && firstSet.contains(SpecialSymbol.EPSILON); i++) {
    		firstSet.remove(SpecialSymbol.EPSILON);
    		firstSet.addAll(retrieveFirst(first, rule.get(i)));
    	}
    	
    	return firstSet;
    }

    public static Map<Nonterminal, Set<GeneralSymbol>> getFollow(Grammar g, Map<Nonterminal, Set<GeneralSymbol>> first) {

        if (g == null || first == null)
            throw new NullPointerException();

        Map<Nonterminal, Set<GeneralSymbol>> follow = initializeNonterminalMapping(g);

        boolean hasChanged;
        do {
        	hasChanged = false;
        	
	        Iterator<Production> productions = g.iterator();
	    	while(productions.hasNext()) {
	    		Nonterminal variable = productions.next().getNonterminal();
	    		
	    		Set<GeneralSymbol> followSet = computeFollow(g, first, follow, variable);

	    		// Qualquer conjunto modificado muda esse booleano para true.
	    		if(!followSet.equals(follow.get(variable))) 
	    			hasChanged = true;	    			
	    		
	    		follow.put(variable, followSet);       		
	    	}

        } while(hasChanged);
        
        return follow;
    }

    
    public static Set<GeneralSymbol> computeFollow(Grammar g, Map<Nonterminal, Set<GeneralSymbol>> first,
    		Map<Nonterminal, Set<GeneralSymbol>> follow, Nonterminal variable) {
		
    	Set<GeneralSymbol> followSet = new HashSet<GeneralSymbol>();
    	
    	// Se X é a variável inicial, então FOLLOW(X) contém $.
    	if(g.getStartSymbol().equals(variable))
    		followSet.add(SpecialSymbol.EOF);
    	
    	Iterator<Production> ruleSet = g.iterator();
    	while(ruleSet.hasNext()) {
    		Production rule = ruleSet.next();
    		
    		for(int i = 0; i < rule.getProduction().size(); i++)
    		
	    		if(rule.getProduction().get(i).equals(variable)) {
	    			
	    			/* Último símbolo da regra (A -> ZX):
	    			 * FOLLOW(X) contém FOLLOW(A). */
	    			if(i == rule.getProduction().size() - 1) 
	    				followSet.addAll(follow.get(rule.getNonterminal()));	    				
	    			
	    			/* Não-último símbolo da regra (A -> ZXY):
	    			 * FOLLOW(X) contém FIRST(Y), excluindo-se a cadeia vazia. */
	    			else if(i < rule.getProduction().size() - 1) {
	    				Set<GeneralSymbol> firstSet = computeFirst(first, rule.getProduction(), i + 1);
	    				followSet.addAll(firstSet);
	    				followSet.remove(SpecialSymbol.EPSILON);
	    				
	    				/* Caso Y derive a cadeia vazia, FOLLOW(X) contém FOLLOW(A). */
	    				if(firstSet.contains(SpecialSymbol.EPSILON))
	    						followSet.addAll(follow.get(rule.getNonterminal()));
	    					
	    			}
	    			
	    		}    			
    	}
    	   	
    	return followSet;
    	
    }

    //método para inicializar mapeamento nãoterminais -> conjunto de símbolos
    private static Map<Nonterminal, Set<GeneralSymbol>>
    initializeNonterminalMapping(Grammar g) {
        Map<Nonterminal, Set<GeneralSymbol>> result =
                new HashMap<Nonterminal, Set<GeneralSymbol>>();

        for (Nonterminal nt: g.getNonterminals())
            result.put(nt, new HashSet<GeneralSymbol>());

        return result;
    }

}
