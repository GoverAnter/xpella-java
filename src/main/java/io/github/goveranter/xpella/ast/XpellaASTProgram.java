package io.github.goveranter.xpella.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class XpellaASTProgram
{
	private List<XpellaASTTypeDeclaration> types;
	
	public XpellaASTProgram()
	{
		this.types = new ArrayList<>();
	}
	
	public XpellaASTProgram(List<XpellaASTTypeDeclaration> types)
	{
		this.types = types;
	}
	
	/**
	 * Returns all types that contains a valid main method.
	 * <em>public static (void | int) main()</em>
	 */
	public List<String> getMainMethodTypes()
	{
		return this.types.stream()
				.filter(type -> type.getMethods().stream()
						.anyMatch(method ->
								(method.getReturnType().equals("void") || method.getReturnType().equals("int")) &&
								method.getIdentifier().equals("main") &&
								method.getVisibility().equals("public") &&
								method.getModifiers().size() == 1 &&
								method.getModifiers().get(0).equals("static")))
				.map(XpellaASTDeclaration::getIdentifier)
				.collect(Collectors.toList());
	}
}
