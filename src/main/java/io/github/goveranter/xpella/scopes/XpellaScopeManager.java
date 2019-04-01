package io.github.goveranter.xpella.scopes;

import io.github.goveranter.xpella.exceptions.scopes.AlreadyDeclaredException;
import io.github.goveranter.xpella.exceptions.scopes.NoContextScopeException;
import io.github.goveranter.xpella.exceptions.scopes.RootChildScopeRemovalException;
import io.github.goveranter.xpella.exceptions.scopes.UndeclaredVariableException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class XpellaScopeManager<T extends ScopeVariable>
{
	private Deque<XpellaScope<T>> scopes;
	
	public XpellaScopeManager()
	{
		this.scopes = new ArrayDeque<>();
	}
	
	private XpellaScope<T> getCurrentScope() throws NoContextScopeException
	{
		if (this.scopes.isEmpty())
			throw new NoContextScopeException();
		
		return this.scopes.peek();
	}
	
	// TODO Allow shadowing option on scope creation
	/**
	 * Creates a scope that will mask the current one, ie. vars from the current scope wont be accessible in the new one.
	 * Use this if you want to create a clean scope, like for a function call.
	 * Use *initialBindings* to put elements in its root scope.
	 */
	public void createMaskingScope(List<T> initialBindings)
	{
		XpellaScope<T> newScope = new XpellaScope<>();
		
		if(initialBindings != null)
		{
			for(T binding : initialBindings)
				newScope.createInScope(binding); // No need to check for errors, as the scope is clean, and nothing can be shadowed
		}
		
		this.scopes.push(newScope);
	}
	
	/**
	 * Use this if you want the variables of the current scope to be available in this one.
	 * This will allow variable masking, as it will not exist in the new one (only on parent scopes).
	 * Use *initialBindings* to prefill it.
	 */
	public void createChildScope(List<T> initialBindings) throws NoContextScopeException
	{
		XpellaScope<T> currentScope = this.getCurrentScope();
		// No need to check for errors, as the scope is clean, and we allow shadowing by default
		currentScope.push(initialBindings);
	}
	
	/** Removes the current masking scope. */
	public void removeMaskingScope() throws NoContextScopeException
	{
		if (this.scopes.isEmpty())
			throw new NoContextScopeException();
		this.scopes.pop();
	}
	
	public void removeChildScope() throws NoContextScopeException, RootChildScopeRemovalException
	{
		if(this.getCurrentScope().pop() == null)
			throw new RootChildScopeRemovalException();
	}
	
	public T getMemoryValue(String identifier) throws NoContextScopeException, UndeclaredVariableException
	{
		return this.getCurrentScope()
				.findInScopes(identifier)
				.orElseThrow(() -> new UndeclaredVariableException(identifier, false));
	}
	// TODO Shadowing options
	/** Use this to create a new variable in memory. */
	public void declareMemoryValue(T variable) throws NoContextScopeException, AlreadyDeclaredException
	{
		if (!this.getCurrentScope().createInScope(variable))
			throw new AlreadyDeclaredException(variable.getIdentifier());
	}
	/** Use this method to assign a value to an already declared variable. */
	public void assignMemoryValue(T variable) throws NoContextScopeException, UndeclaredVariableException
	{
		// Cannot assign an undeclared variable
		if (!this.getCurrentScope().setInScopes(variable))
			throw new UndeclaredVariableException(variable.getIdentifier(), true);
	}
	/** Similar to call exists(identifier, false) */
	public boolean exists(String identifier) throws NoContextScopeException
	{
		return exists(identifier, false);
	}
	public boolean exists(String identifier, boolean canShadow) throws NoContextScopeException
	{
		if(canShadow)
			return this.getCurrentScope().existsInCurrentScope(identifier);
		else
			return this.getCurrentScope().existsInScopes(identifier);
	}
}
