package io.github.goveranter.xpella.ast;

import java.util.ArrayList;
import java.util.List;

public abstract class XpellaASTNode
{
	private List<XpellaASTAnnotation> annotations;
	private String documentation;
	
	public XpellaASTNode()
	{
		this(new ArrayList<>(), "");
	}
	
	public XpellaASTNode(List<XpellaASTAnnotation> annotations, String documentation)
	{
		this.annotations = annotations;
		this.documentation = documentation;
	}
	
	public List<XpellaASTAnnotation> getAnnotations()
	{
		return annotations;
	}
	public String getDocumentation()
	{
		return documentation;
	}
	public boolean isRuntimeAST()
	{
		return documentation == null;
	}
}
