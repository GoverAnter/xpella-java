package io.github.goveranter.xpella.ast;

import java.util.List;

public class XpellaASTBlock extends XpellaASTStatement
{
	private List<XpellaASTStatement> statements;
	
	public XpellaASTBlock(List<XpellaASTStatement> statements)
	{
		super();
		this.statements = statements;
	}
	
	public XpellaASTBlock(List<XpellaASTAnnotation> annotations, String documentation, List<XpellaASTStatement> statements)
	{
		super(annotations, documentation);
		this.statements = statements;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof XpellaASTBlock)
		{
			XpellaASTBlock block = (XpellaASTBlock)other;
			if(block.statements.size() == this.statements.size())
			{
				for(int i = 0; i < this.statements.size(); i++)
				{
					if(!block.statements.get(i).equals(this.statements.get(i)))
						return false;
				}
				return true;
			}
		}
		
		return false;
	}
	
	public List<XpellaASTStatement> getStatements()
	{
		return statements;
	}
}
