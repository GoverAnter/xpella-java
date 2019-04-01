package io.github.goveranter.xpella.exceptions.scopes;

public class RootChildScopeRemovalException extends ScopeException
{
	public RootChildScopeRemovalException()
	{
		super("Cannot remove a masking scope's root child scope, remove the masking scope instead");
	}
}
