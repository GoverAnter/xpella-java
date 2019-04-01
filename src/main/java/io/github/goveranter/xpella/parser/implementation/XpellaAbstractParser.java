package io.github.goveranter.xpella.parser.implementation;

import io.github.goveranter.xpella.ast.XpellaASTProgram;
import io.github.goveranter.xpella.ast.XpellaASTTypeDeclaration;
import io.github.goveranter.xpella.exceptions.parser.MemberNotFoundException;
import io.github.goveranter.xpella.exceptions.parser.TypeNotFoundException;
import io.github.goveranter.xpella.parser.XpellaParserWarning;
import io.github.goveranter.xpella.parser.headers.XpellaParserArgumentHeader;
import io.github.goveranter.xpella.parser.headers.XpellaParserOperatorHeader;
import io.github.goveranter.xpella.parser.headers.XpellaParserTypeHeader;
import io.github.goveranter.xpella.parser.headers.XpellaParserVariableHeader;
import io.github.goveranter.xpella.parser.helpers.XpellaInputStream;
import io.github.goveranter.xpella.parser.helpers.XpellaLexer;
import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;
import io.github.goveranter.xpella.scopes.XpellaScopeManager;

import java.util.*;

public abstract class XpellaAbstractParser
{
	// Parser input
	protected String sourceCode;
	
	// Parser helpers
	protected XpellaInputStream inputStream;
	protected XpellaLexer lexer;
	
	protected final List<XpellaParserTypeHeader> defaultTypes = Collections.unmodifiableList(List.of(
			new XpellaParserTypeHeader("int", new ArrayList<>(), new ArrayList<>(), Map.ofEntries(
					Map.entry("+", new XpellaParserOperatorHeader(false, false, List.of("int"))),
					Map.entry("-", new XpellaParserOperatorHeader(false, false, List.of("int"))),
					Map.entry("/", new XpellaParserOperatorHeader(false, false, List.of("int"))),
					Map.entry("*", new XpellaParserOperatorHeader(false, false, List.of("int"))),
					Map.entry("%", new XpellaParserOperatorHeader(false, false, List.of("int"))),
					Map.entry("<", new XpellaParserOperatorHeader(false, false, List.of("int", "float"))),
					Map.entry(">", new XpellaParserOperatorHeader(false, false, List.of("int", "float"))),
					Map.entry("<=", new XpellaParserOperatorHeader(false, false, List.of("int", "float"))),
					Map.entry(">=", new XpellaParserOperatorHeader(false, false, List.of("int", "float"))),
					Map.entry("++", new XpellaParserOperatorHeader(true, true, new ArrayList<>())),
					Map.entry("--", new XpellaParserOperatorHeader(true, true, new ArrayList<>())),
					Map.entry("=", new XpellaParserOperatorHeader(false, false, List.of("int", "boolean")))
			)),
			new XpellaParserTypeHeader("string", new ArrayList<>(), new ArrayList<>(), Map.ofEntries(
					Map.entry("+", new XpellaParserOperatorHeader(false, false, List.of("string", "int", "float", "boolean"))),
					Map.entry("=", new XpellaParserOperatorHeader(false, false, List.of("string", "int", "float", "boolean")))
			)),
			new XpellaParserTypeHeader("boolean", new ArrayList<>(), new ArrayList<>(), Map.ofEntries(
					Map.entry("&&", new XpellaParserOperatorHeader(false, false, List.of("boolean"))),
					Map.entry("||", new XpellaParserOperatorHeader(false, false, List.of("boolean"))),
					Map.entry("!", new XpellaParserOperatorHeader(false, true, new ArrayList<>())),
					Map.entry("=", new XpellaParserOperatorHeader(false, false, List.of("boolean", "int")))
			))
	));
	
	protected XpellaScopeManager<XpellaParserVariableHeader> scopeManager;
	
	protected XpellaASTTypeDeclaration currentThis;
	
	// Parser output
	protected List<XpellaParserWarning> warnings = new ArrayList<>();
	protected XpellaASTProgram ast;
	protected List<XpellaParserTypeHeader> declaredTypes = new ArrayList<>();
	
	public XpellaAbstractParser(String sourceCode)
	{
		this.sourceCode = sourceCode;
		
		this.inputStream = new XpellaInputStream(sourceCode);
		this.lexer = new XpellaLexer(this.inputStream);
		
		this.scopeManager = new XpellaScopeManager<>();
		
		this.ast = new XpellaASTProgram();
	}
	
	public List<XpellaParserWarning> getWarnings()
	{
		return this.warnings;
	}
	
	public XpellaASTProgram getAST()
	{
		return this.ast;
	}
	
	protected void addWarning(String message, XpellaParserBookmark bookmark)
	{
		// TODO Print this warning as the compilation goes
		this.warnings.add(new XpellaParserWarning(message, bookmark));
	}
	
	protected Optional<XpellaParserTypeHeader> getLanguageType(String identifier)
	{
		return this.defaultTypes.stream()
				.filter(type -> type.getIdentifier().equals(identifier))
				.findAny();
	}
	
	protected Optional<XpellaParserTypeHeader> getDeclaredType(String identifier)
	{
		return this.declaredTypes.stream()
				.filter(type -> type.getIdentifier().equals(identifier))
				.findAny();
	}
	
	protected XpellaParserVariableHeader getMemberInType(String type, String member) throws TypeNotFoundException, MemberNotFoundException
	{
		XpellaParserTypeHeader t = this.getType(type);
		return t.getMembers().stream()
				.filter(mem -> mem.getIdentifier().equals(member))
				.findAny()
				.orElseThrow(() -> new MemberNotFoundException(member, type, new XpellaParserBookmark(this.inputStream)));
	}
	
	protected XpellaParserTypeHeader getType(String identifier) throws TypeNotFoundException
	{
		if(this.currentThis.getIdentifier().equals(identifier))
			return XpellaParserTypeHeader.fromDeclaration(this.currentThis);
		
		return this.getLanguageType(identifier)
				.or(() -> this.getDeclaredType(identifier))
				.orElseThrow(() -> new TypeNotFoundException(identifier, new XpellaParserBookmark(this.inputStream)));
	}
	
	protected boolean typeExists(String identifier)
	{
		return this.getLanguageType(identifier)
				.or(() -> this.getDeclaredType(identifier))
				.isPresent();
	}
	
	protected boolean compareArgsArrays(List<XpellaParserArgumentHeader> arr1, List<String> arr2)
	{
		for(int i = 0; i < arr1.size(); i++)
		{
			if(i == arr2.size())
				return i >= arr1.size() - 1 || arr1.get(i + 1).hasDefault();
			else if(!arr1.get(i).getType().equals(arr2.get(i)))
				return false;
		}
		
		return true;
	}
	
	/** Small helper method to compare the content of two arrays */
	protected boolean arrayEqual(List<?> arr1, List<?> arr2)
	{
		if(arr1.size() != arr2.size())
			return false;
		for(int i = 0; i < arr1.size(); i++)
		{
			if(!arr1.get(i).equals(arr2.get(i)))
				return false;
		}
		
		return true;
	}
}
