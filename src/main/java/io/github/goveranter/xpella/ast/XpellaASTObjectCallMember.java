package io.github.goveranter.xpella.ast;

import java.util.List;

public class XpellaASTObjectCallMember extends XpellaASTObjectCall
{
	private XpellaASTVariable member;
	
	public XpellaASTObjectCallMember(XpellaASTExpression object, XpellaASTVariable member)
	{
		super(member.getResolvedType(), object);
		this.member = member;
	}
	
	public XpellaASTObjectCallMember(List<XpellaASTAnnotation> annotations, String documentation, XpellaASTExpression object, XpellaASTVariable member)
	{
		super(annotations, documentation, member.getResolvedType(), object);
		this.member = member;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof XpellaASTObjectCallMember)
		{
			XpellaASTObjectCallMember call = (XpellaASTObjectCallMember)other;
			return call.getObject().equals(this.getObject()) && call.member.equals(this.member);
		}
		
		return false;
	}
	
	public XpellaASTVariable getMember()
	{
		return member;
	}
}
