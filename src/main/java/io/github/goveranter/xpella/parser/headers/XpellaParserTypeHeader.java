package io.github.goveranter.xpella.parser.headers;

import io.github.goveranter.xpella.ast.XpellaASTTypeDeclaration;
import io.github.goveranter.xpella.parser.XpellaParserRules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class XpellaParserTypeHeader
{
	public static XpellaParserTypeHeader fromDeclaration(XpellaASTTypeDeclaration declaration)
	{
		List<XpellaParserFunctionHeader> methods = declaration.getMethods().stream()
				.map(XpellaParserFunctionHeader::fromDeclaration)
				.collect(Collectors.toList());
		
		return new XpellaParserTypeHeader(
				declaration.getIdentifier(),
				declaration.getMembers().stream()
						.filter(member -> !member.getVisibility().equals("private"))
						.map(XpellaParserMemberHeader::fromDeclaration)
						.collect(Collectors.toList()),
				methods.stream()
						.filter(method -> !method.getIdentifier().startsWith(XpellaParserRules.OPERATOR_METHOD_PREFIX))
						.collect(Collectors.toList()),
				methods.stream()
						.filter(method -> method.getIdentifier().startsWith(XpellaParserRules.OPERATOR_METHOD_PREFIX))
						.collect(HashMap::new, (map, header) -> {
							List<String> list = new ArrayList<>();
							list.add(header.getArgs().size() == 0 ? "void" : header.getArgs().get(0).getType());
							
							String identifier = header.getIdentifier().replace(XpellaParserRules.OPERATOR_METHOD_PREFIX, "");
							
							// TODO Determine if operator is front or back
							if(map.containsKey(identifier))
								map.get(identifier).getAcceptedTypes().addAll(list);
							else
								map.put(identifier, new XpellaParserOperatorHeader(false, false, list));
						}, (target, source) -> {
							for(Map.Entry<String, XpellaParserOperatorHeader> entry : source.entrySet())
							{
								if(target.containsKey(entry.getKey()))
									target.merge(entry.getKey(),
											entry.getValue(),
											(t, s) -> { t.getAcceptedTypes().addAll(s.getAcceptedTypes()); return t; });
								else
									target.put(entry.getKey(), entry.getValue());
							}
						}));
	}
	
	private String identifier;
	private List<XpellaParserMemberHeader> members;
	private List<XpellaParserFunctionHeader> methods;
	// operator -> list of accepted types
	private Map<String, XpellaParserOperatorHeader> operators;
	
	public XpellaParserTypeHeader(String identifier,
								  List<XpellaParserMemberHeader> members,
								  List<XpellaParserFunctionHeader> methods,
								  Map<String, XpellaParserOperatorHeader> operators)
	{
		this.identifier = identifier;
		this.members = members;
		this.methods = methods;
		this.operators = operators;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		// TODO Implement me
		return super.equals(obj);
	}
	
	public String getIdentifier()
	{
		return identifier;
	}
	public List<XpellaParserMemberHeader> getMembers()
	{
		return members;
	}
	public List<XpellaParserFunctionHeader> getMethods()
	{
		return methods;
	}
	public Map<String, XpellaParserOperatorHeader> getOperators()
	{
		return operators;
	}
}
