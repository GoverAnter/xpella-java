package io.github.goveranter.xpella.exceptions.scopes;

import io.github.goveranter.xpella.exceptions.XpellaException;

public abstract class ScopeException extends XpellaException
{
	public ScopeException(String message)
	{
		super(message);
	}
}
