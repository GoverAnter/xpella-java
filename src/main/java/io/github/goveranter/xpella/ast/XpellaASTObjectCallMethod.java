package io.github.goveranter.xpella.ast;

import java.util.List;

public class XpellaASTObjectCallMethod extends XpellaASTObjectCall
{
	private XpellaASTFunctionCall method;
	
	public XpellaASTObjectCallMethod(XpellaASTExpression object, XpellaASTFunctionCall method)
	{
		super(method.getResolvedType(), object);
		this.method = method;
	}
	
	public XpellaASTObjectCallMethod(List<XpellaASTAnnotation> annotations, String documentation, XpellaASTExpression object, XpellaASTFunctionCall method)
	{
		super(annotations, documentation, method.getResolvedType(), object);
		this.method = method;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof XpellaASTObjectCallMethod)
		{
			XpellaASTObjectCallMethod call = (XpellaASTObjectCallMethod)other;
			return call.getObject().equals(this.getObject()) && call.method.equals(this.method);
		}
		
		return false;
	}
	
	public XpellaASTFunctionCall getMethod()
	{
		return method;
	}
}
