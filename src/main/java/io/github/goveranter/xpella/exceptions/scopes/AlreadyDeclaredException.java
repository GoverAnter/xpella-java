package io.github.goveranter.xpella.exceptions.scopes;

public class AlreadyDeclaredException extends ScopeException
{
	public AlreadyDeclaredException(String identifier)
	{
		super("Variable \"" + identifier + "\" is already declared and cannot be shadowed");
	}
}
