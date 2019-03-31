package io.github.goveranter.xpella.ast;

import java.util.List;

public abstract class XpellaASTStatement extends XpellaASTNode
{
	public XpellaASTStatement()
	{
		super();
	}
	
	public XpellaASTStatement(List<XpellaASTAnnotation> annotations, String documentation)
	{
		super(annotations, documentation);
	}
}
