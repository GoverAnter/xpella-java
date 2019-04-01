package io.github.goveranter.xpella.scopes;

import java.util.*;

public class XpellaScope<T extends ScopeVariable>
{
	private final Deque<Map<String, T>> scopesStack;
	
	public XpellaScope()
	{
		this.scopesStack = new ArrayDeque<>();
		// This is the root scope, cannot be removed, needs to be present
		this.scopesStack.push(new HashMap<>());
	}
	
	/**
	 * Adds a new scope to the stack.
	 * Use *initialBindings* to prefill it.
	 */
	public void push(List<T> initialBindings)
	{
		Map<String, T> newScope = new HashMap<>();
		
		if(initialBindings != null)
		{
			for(T binding : initialBindings)
				newScope.put(binding.getIdentifier(), binding);
		}
		
		this.scopesStack.push(newScope);
	}
	
	/** Removes the current scope. Returns null if nothing was removed. Will not remove the root (first) scope. */
	public Map<String, T> pop()
	{
		// Cannot remove the root scope
		if (this.scopesStack.size() == 1)
			return null;
		else
			return this.scopesStack.pop();
	}
	
	// Every operation on the stack is "safe" because it MUST (by design) have AT LEAST ONE scope
	
	/** Returns the current scope. Will never be null, but can be empty. */
	private Map<String, T> getCurrentScope()
	{
		return this.scopesStack.peek();
	}
	
	/** Returns the number of child scopes the context scope has. Will be at least 1, as root scope can't be deleted. */
	public int getScopesCount()
	{
		return this.scopesStack.size();
	}
	
	/** Returns null if nothing was found. */
	public Optional<T> findInCurrentScope(String identifier)
	{
		return Optional.ofNullable(this.getCurrentScope().get(identifier));
	}
	
	/** Returns null if nothing was found. */
	public Optional<T> findInScopes(String identifier)
	{
		for(Map<String, T> map : this.scopesStack)
		{
			if(map.containsKey(identifier))
				return Optional.of(map.get(identifier));
		}
		
		return Optional.empty();
	}
	
	/**
	 * Tests only in the current scope if the provided name is known.
	 * Use *existsInScopes* to check in all scopes instead.
	 */
	public boolean existsInCurrentScope(String identifier)
	{
		return this.getCurrentScope().containsKey(identifier);
	}
	
	/**
	 * Backward checks in all stacks to see if the variable named with the identifier is defined.
	 */
	public boolean existsInScopes(String identifier)
	{
		for(Map<String, T> map : this.scopesStack)
		{
			if(map.containsKey(identifier))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Adds the provided variable to the current scope. Returns false if the variable was not added, because it already exists.
	 * Allows you to declare a variable with the same name of an already declared variable,
	 * which was declared in a parent scope, effectively shadowing it.
	 * Similar to call createInScope(variable, true);
	 */
	public boolean createInScope(T variable)
	{
		return this.createInScope(variable, true);
	}
	
	/**
	 * Adds the provided variable to the current scope. Returns false if the variable was not added, because it already exists.
	 * *allowShadowing* allows you to declare a variable with the same name of an already declared variable,
	 * which was declared in a parent scope, effectively shadowing it.
	 */
	public boolean createInScope(T variable, boolean allowShadowing)
	{
		if((allowShadowing && this.existsInCurrentScope(variable.getIdentifier())) || this.existsInScopes(variable.getIdentifier()))
			return false;
		
		this.getCurrentScope().put(variable.getIdentifier(), variable);
		return true;
	}
	
	/**
	 * Will try to set the value of a variable in the current scope only.
	 * If the variable does not exist in the current scope, this method will return false.
	 * Also means this method can return false even if the variable exists, but not in the current scope.
	 */
	public boolean setInCurrentScopeOnly(T variable)
	{
		if (!this.existsInCurrentScope(variable.getIdentifier()))
			return false;
		
		this.getCurrentScope().put(variable.getIdentifier(), variable);
		return true;
	}
	
	/**
	 * Will try to set the value of a variable in all scopes.
	 * If the variable does not exist in some scope, this method will return false.
	 * The value will be replaced in the scope the value was in initially (which means it will not be placed in the current scope).
	 */
	public boolean setInScopes(T variable)
	{
		for(Map<String, T> map : this.scopesStack)
		{
			if(map.containsKey(variable.getIdentifier()))
			{
				map.put(variable.getIdentifier(), variable);
				return true;
			}
		}
		
		return false;
	}
}
