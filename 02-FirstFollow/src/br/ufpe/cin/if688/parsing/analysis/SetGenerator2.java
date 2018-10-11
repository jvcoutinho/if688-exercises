package br.ufpe.cin.if688.parsing.analysis;

import java.util.*;

import br.ufpe.cin.if688.parsing.grammar.*;


public final class SetGenerator2 {

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
    
    /**
     * Função que retorna o conjunto FIRST já existente de uma variável (ou retorna o caso base de um terminal).
     * @param first - Conjunto FIRST
     * @param symbol - Símbolo (terminal ou variável)
     * @return Set<GeneralSymbol> FIRST
     */
    public static Set<GeneralSymbol> retrieveFirst(Map<Nonterminal, Set<GeneralSymbol>> first, GeneralSymbol symbol) {
    	
    	Set<GeneralSymbol> firstSet = new HashSet<GeneralSymbol>();
    	
    	/* Se X é um terminal, FIRST(X) = { X }
    	*  Se X -> ^ é um produção de X, ^ pertence a FIRST(X). */
    	if(symbol instanceof Terminal || symbol.equals(SpecialSymbol.EPSILON)) {
    		firstSet.add(symbol);
    		return firstSet;
    	} 
		
    	firstSet.addAll(first.get(symbol));
    	return firstSet;
    	
    }
    
    /**
     * Função que computa o conjunto FIRST de uma variável.
     * @param Grammar g
     * @param Map<Nonterminal, Set<GeneralSymbol>> first
     * @param GeneralSymbol symbol
     * @return Set<GeneralSymbol> FIRST
     */
    public static Set<GeneralSymbol> computeFirst(Grammar g, Map<Nonterminal, Set<GeneralSymbol>> first, GeneralSymbol symbol) {
    	
    	Set<GeneralSymbol> firstSet = new HashSet<GeneralSymbol>();
    	
    	Iterator<Production> rules = g.iterator();
    	while(rules.hasNext()) {
    		Production rule = rules.next();
    		
    		if(rule.getNonterminal().equals(symbol)) {
    
    			Iterator<GeneralSymbol> symbols = rule.getProduction().iterator();
    			Set<GeneralSymbol> firstS = retrieveFirst(first, symbols.next());
    			
    			while(symbols.hasNext() && firstS.contains(SpecialSymbol.EPSILON)) {
    				firstSet.addAll(firstS);
    				firstSet.remove(SpecialSymbol.EPSILON);
    				firstS = retrieveFirst(first, symbols.next());
    			}
    			
    			firstSet.addAll(firstS);    			    				
    		}

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
	    		if(!followSet.equals(follow.get(variable))) {
	    			hasChanged = true;	    			
	    		}
	    		
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
    	
    	Iterator<Production> rules = g.iterator();
    	while(rules.hasNext()) {
    		Production rule = rules.next();
    		
    		for(int i = 0; i < rule.getProduction().size(); i++)
    		
	    		if(rule.getProduction().get(i).equals(variable)) {
	    			
	    			/* Último símbolo da regra (A -> ZX):
	    			 * FOLLOW(X) contém FOLLOW(A). */
	    			if(i == rule.getProduction().size() - 1) {
	    				followSet.addAll(follow.get(rule.getNonterminal()));
	    			}
	    			
	    			/* Não-último símbolo da regra (A -> ZXY):
	    			 * FOLLOW(X) contém FIRST(Y), excluindo-se a cadeia vazia. */
	    			else if(i < rule.getProduction().size() - 1) {
	    				Set<GeneralSymbol> firstSet = retrieveFirst(first, rule.getProduction().get(i + 1));
	    				followSet.addAll(firstSet);
	    				followSet.remove(SpecialSymbol.EPSILON);
	    				
	    				/* Caso Y derive a cadeia vazia, FOLLOW(X) contém FOLLOW(A). */
	    				if(derivesEmptyString(first, rule.getProduction(), i + 1)) 
	    						followSet.addAll(follow.get(rule.getNonterminal()));
	    					
	    			}
	    			
	    		} 
    			
    	}
    	   	
    	return followSet;
    	
    }
    
    private static boolean derivesEmptyString(Map<Nonterminal, Set<GeneralSymbol>> first, List<GeneralSymbol> rule, int symbolIndex) {
    	for(int i = symbolIndex; i < rule.size(); i++)
    		if(!retrieveFirst(first, rule.get(i)).contains(SpecialSymbol.EPSILON))
    			return false;
    	return true;
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
