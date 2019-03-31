package io.github.goveranter.xpella.exceptions;

public abstract class XpellaException extends Exception
{
	public XpellaException()
	{
		super();
	}
	
	public XpellaException(String message)
	{
		super(message);
	}
	
	public XpellaException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public XpellaException(Throwable cause)
	{
		super(cause);
	}
	
	public XpellaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
