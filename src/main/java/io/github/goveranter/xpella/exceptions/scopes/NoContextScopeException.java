package io.github.goveranter.xpella.exceptions.scopes;

public class NoContextScopeException extends ScopeException
{
	public NoContextScopeException()
	{
		super("No scopes defined in this context");
	}
}
