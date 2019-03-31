package io.github.goveranter.xpella.exceptions.scopes;

/**
 * Thrown when trying to get a variable that does not exists from a child scope.
 * Thrown when trying to assign a variable that is not declared in the current context scope.
 */
public class UndeclaredVariableException extends ScopeException
{
	public UndeclaredVariableException(String identifier, boolean contextScope)
	{
		super("Variable \"" + identifier + "\" is undeclared in the current " + (contextScope ? "context" : "child") + " scope");
	}
}
