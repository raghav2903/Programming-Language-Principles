package cop5556sp17;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

import cop5556sp17.TypeCheckVisitor.TypeCheckException;
import cop5556sp17.AST.*;

import cop5556sp17.AST.Dec;

public class SymbolTable {
	
	public class SymbolEntry {

		private Dec dec;
		private int scope;

		public SymbolEntry(Dec dec, int scope) {
			this.dec = dec;
			this.scope = scope;
		}
		
		public Dec getDec() {
			return dec;
		}
		public void setDec(Dec dec) {
			this.dec = dec;
		}
		public int getScope() {
			return scope;
		}
		public void setScope(int scope) {
			this.scope = scope;
		}
		
		
	}


	HashMap<String, LinkedList<SymbolEntry>> testMap =  new HashMap<String, LinkedList<SymbolEntry>>();
	int current_scope;
	int next_scope;
    Stack<Integer> scope_stack = new Stack<Integer>();
	/** 
	 * to be called when block entered
	 */
	public void enterScope(){
		current_scope = next_scope++; 
		scope_stack.push(current_scope);
	}
	
	
	/**
	 * leaves scope
	 */
	public void leaveScope(){
		scope_stack.pop();
		current_scope = scope_stack.peek();
	}
	
	public boolean insert(String ident, Dec dec){
		boolean insert_indicator = false;
		
		if(testMap.containsKey(ident))
		{ 
			LinkedList<SymbolEntry> att_list = testMap.get(ident);
			for(SymbolEntry sy: att_list)
			{
				if(sy.getScope()!= current_scope)
				{
					insert_indicator = true;
				}
				else
				{
					insert_indicator = false;
				}
			}
			
			if(insert_indicator == true)
			{
				SymbolEntry symbol_entry = new SymbolEntry(dec,current_scope);
				att_list.add(symbol_entry);
			}
			
		}
		else
		{
			SymbolEntry symbol_entry = new SymbolEntry(dec,current_scope);
			LinkedList<SymbolEntry> list = new LinkedList<SymbolEntry>();
			list.add(symbol_entry);
			testMap.put(ident, list);
			insert_indicator = true;
		}
		
		return insert_indicator;
	}
	
	public Dec lookup(String ident) throws Exception{
		Dec val = null;
		
		if(testMap.containsKey(ident))
		{
			LinkedList<SymbolEntry> att_list = testMap.get(ident);
			for(int i = scope_stack.size()-1; i >=0 ; i--)
			{
				for( SymbolEntry sy: att_list )
				{
					if(sy.getScope() == scope_stack.get(i))
					{
						val = sy.getDec();
						return val;
					}
				}
			}
		}
		else
		{
			val = null;
		}
		
		return val;
	}
		
	public SymbolTable() {
		current_scope= 0;
		next_scope = 1;
		scope_stack.push(0);
	}


	@Override
	public String toString() {
		return "SymbolTable [SymbolTable = " + testMap + "]";
	}
	
	


}
