package io.github.goveranter.xpella.ast;

import java.util.List;

public class XpellaASTCondition extends XpellaASTStatement
{
	private XpellaASTExpression condition;
	private XpellaASTStatement passConditionExecution;
	private XpellaASTStatement failConditionExecution;
	
	public XpellaASTCondition(XpellaASTExpression condition, XpellaASTStatement passConditionExecution, XpellaASTStatement failConditionExecution)
	{
		super();
		this.condition = condition;
		this.passConditionExecution = passConditionExecution;
		this.failConditionExecution = failConditionExecution;
	}
	
	public XpellaASTCondition(List<XpellaASTAnnotation> annotations, String documentation, XpellaASTExpression condition, XpellaASTStatement passConditionExecution, XpellaASTStatement failConditionExecution)
	{
		super(annotations, documentation);
		this.condition = condition;
		this.passConditionExecution = passConditionExecution;
		this.failConditionExecution = failConditionExecution;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof XpellaASTCondition)
		{
			// TODO Take nulls into account
			XpellaASTCondition cond = (XpellaASTCondition)other;
			return cond.condition.equals(this.condition) &&
					cond.passConditionExecution.equals(this.passConditionExecution) &&
					cond.failConditionExecution.equals(this.failConditionExecution);
		}
		
		return false;
	}
	
	public XpellaASTExpression getCondition()
	{
		return condition;
	}
	public XpellaASTStatement getPassConditionExecution()
	{
		return passConditionExecution;
	}
	public XpellaASTStatement getFailConditionExecution()
	{
		return failConditionExecution;
	}
}
