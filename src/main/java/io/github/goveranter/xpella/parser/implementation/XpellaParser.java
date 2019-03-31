package io.github.goveranter.xpella.parser.implementation;

import io.github.goveranter.xpella.ast.*;
import io.github.goveranter.xpella.exceptions.parser.*;
import io.github.goveranter.xpella.exceptions.scopes.ScopeException;
import io.github.goveranter.xpella.parser.XpellaParserRules;
import io.github.goveranter.xpella.parser.headers.XpellaParserTypeHeader;
import io.github.goveranter.xpella.parser.headers.XpellaParserVariableHeader;
import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class XpellaParser extends XpellaAbstractExpressionParser
{
	private String currentFunctionExpectedReturnType;
	
	public XpellaParser(String sourceCode)
	{
		super(sourceCode);
	}
	
	// This is the main parse method
	public XpellaASTProgram executeParse()
			throws UnmetExpectationException, UnexpectedCharacterException, TypeRedeclarationException, NoThisException,
			MethodNotFoundException, WrongMethodArgumentsException, WrongOperatorArgumentTypeException, ScopeException,
			NoDeclaredOperatorException, MemberNotFoundException, NoMethodReturnTypeException, StaticCallException,
			TypeNotFoundException, NotAFunctionException, NameReservedException
	{
		List<XpellaASTTypeDeclaration> types = new ArrayList<>();
		this.lexer.skipWhitespaces(); // Eat whitespaces
		
		while(!this.inputStream.isEof())
		{
			types.add(this.parseType());
			this.lexer.skipWhitespaces(); // Eat whitespaces
		}
		
		return new XpellaASTProgram(types);
	}
	
	public XpellaASTTypeDeclaration parseType()
			throws UnmetExpectationException, UnexpectedCharacterException, TypeRedeclarationException, NoThisException,
			MethodNotFoundException, WrongMethodArgumentsException, WrongOperatorArgumentTypeException, ScopeException,
			NoDeclaredOperatorException, MemberNotFoundException, NoMethodReturnTypeException, StaticCallException,
			TypeNotFoundException, NotAFunctionException, NameReservedException
	{
		this.lexer.skipWhitespaces(); // Eat whitespaces
		
		this.lexer.eatWord(XpellaParserRules.TYPE_DECLARATION_KEYWORD);
		this.lexer.skipWhitespaces(true); // Eat whitespaces
	
		String typeName = this.lexer.readWord();
		
		if(this.typeExists(typeName))
		{
			this.inputStream.rewind(this.inputStream.getCurrentPosition() - typeName.length());
			throw new TypeRedeclarationException(typeName, new XpellaParserBookmark(this.inputStream));
		}
		
		this.lexer.skipWhitespaces(false); // Eat whitespaces
		this.lexer.eatChar("{");
		this.lexer.skipWhitespaces(false); // Eat whitespaces
		
		this.currentThis = new XpellaASTTypeDeclaration(typeName, new ArrayList<>(), new ArrayList<>());
		
		// Find all declarations in this type
		while(!this.inputStream.peek().equals("}"))
		{
			// Read every word we find
			boolean wasEmpty = false;
			List<XpellaParserBookmark> positions = new ArrayList<>();
			List<String> words = new ArrayList<>();
			
			do {
				positions.add(new XpellaParserBookmark(this.inputStream));
				String currentWord = this.lexer.readWord();
				if(currentWord.isEmpty())
					wasEmpty = true;
				else
					words.add(currentWord);
				
				this.lexer.skipWhitespaces(); // Eat whitespaces
			} while(!wasEmpty);
			
			// words must be at least 2 long
			if(words.size() == 0)
				throw new UnmetExpectationException("Expected type, got \"" + this.inputStream.peek() + "\"", new XpellaParserBookmark(this.inputStream));
			else if (words.size() == 1)
				throw new UnmetExpectationException("Expected identifier, got \"" + this.inputStream.peek() + "\"", new XpellaParserBookmark(this.inputStream));
			
			String name;
			String type;
			String visibility;
			List<String> modifiers = new ArrayList<>();
			for(int i = 0; i < words.size(); i++) {
				String currentWord = words.get(i);
				if (i == words.size() - 1)
				{
					// This is our identifier
					this.checkReserved(currentWord, positions.get(i));
					if(this.currentThis.getMethods().stream().anyMatch(meth -> meth.getIdentifier().equals(currentWord)))
					{
						this.inputStream.rewind(positions.get(i).getPosition());
						this.inputStream.throw('XP0415: "' + currentWord + '" is already declared as a method of this class');
					}
					if(this.currentThis.getMembers().stream().anyMatch(mem -> mem.getIdentifier().equals(currentWord)))
					{
						this.inputStream.rewind(positions.get(i).getPosition());
						this.inputStream.throw('XP0416: "' + currentWord + '" is already declared as a member of this class');
					}
					name = currentWord;
				}
				else if (i == words.size() - 2)
				{
					// This is our type
					if(!this.typeExists(currentWord) && !currentWord.equals("void"))
					{
						this.inputStream.rewind(positions.get(i).getPosition());
						throw new TypeNotFoundException(currentWord, new XpellaParserBookmark(this.inputStream));
					}
					type = currentWord;
				}
				else
				{
					// This is a modifier (either visibility or not)
					if(XpellaParserRules.VISIBILITIES.contains(currentWord))
					{
						// This is a visibility keyword, throw if we already have one
						if(visibility != null)
						{
							this.inputStream.rewind(positions.get(i).getPosition());
							this.inputStream.throw('XP0418: Having more than one visibility modifier is forbidden');
						}
						visibility = currentWord;
					}
					else
					{
						// Should be a modifier
						if(!XpellaParserRules.MODIFIERS.contains(currentWord))
						{
							this.inputStream.rewind(positions.get(i).getPosition());
							this.inputStream.throw('XP0419: "' + currentWord + '" is not a valid modifier');
						}
						modifiers.add(currentWord);
					}
				}
			}
			
			if(this.inputStream.peek().equals("("))
			{
				// TODO Handle const methods, to prevent side-effects compile-time
				// This is a method declaration
				this.currentFunctionExpectedReturnType = type;
				List<XpellaASTVariableDeclaration> args = this.parseSimpleVariableDeclarationList(typeName);
				this.scopeManager.createMaskingScope(args.stream()
						.map(XpellaParserVariableHeader::fromDeclaration)
						.collect(Collectors.toList()));
				
				this.currentThis.getMethods().add(new XpellaASTFunctionDeclaration(name, visibility, modifiers, type,
						args, (XpellaASTBlock)this.parseBlock(true)));
				this.scopeManager.removeMaskingScope();
				this.currentFunctionExpectedReturnType = null; // Just in case...
			}
			else
			{
				// This is a member declaration
				XpellaASTExpression initialAssignation = null;
				if(this.inputStream.peek().equals("="))
				{
					this.lexer.eatChar("=");
					initialAssignation = this.parseExpression(0);
				}
				
				this.currentThis.getMembers().add(new XpellaASTVariableDeclaration(name, type, visibility, modifiers, initialAssignation));
				
				this.lexer.skipWhitespaces(); // Eat whitespaces
				this.lexer.eatChar(XpellaParserRules.LINE_DELIMITER);
			}
			
			this.lexer.skipWhitespaces(false); // Eat whitespaces
		}
		
		this.lexer.eatChar("}");
	
		XpellaASTTypeDeclaration typeDeclaration = this.currentThis;
		this.currentThis = null; // Set it just in case
		this.declaredTypes.add(XpellaParserTypeHeader.fromDeclaration(typeDeclaration));
		return typeDeclaration;
	}
	
	public List<XpellaASTVariableDeclaration> parseSimpleVariableDeclarationList(String currentType)
			throws UnexpectedCharacterException, UnmetExpectationException
	{
		this.lexer.skipWhitespaces(); // Eat whitespaces
		this.lexer.eatChar("(");
		this.lexer.skipWhitespaces(); // Eat whitespaces
	
		List<XpellaASTVariableDeclaration> list = new ArrayList<>();
		
		while (!this.inputStream.peek().equals(")"))
		{
			// We need exactly 2 words : a type and a name
		 	String type = this.lexer.readWord();
			
			if(type.isEmpty())
				throw new UnmetExpectationException("Unexpected \"" + this.inputStream.peek() + "\"",
						new XpellaParserBookmark(this.inputStream));
			else if(!this.typeExists(type) && !type.equals(currentType))
			{
				this.inputStream.rewind(this.inputStream.getCurrentPosition() - type.length());
				this.inputStream.throw('XP041B: Type "' + type + '" does not exists');
			}
			
			this.lexer.skipWhitespaces(); // Eat whitespaces
		  	String name = this.lexer.readWord();
			
			if(name.isEmpty())
				throw new UnmetExpectationException("Unexpected \"" + this.inputStream.peek() + "\"",
						new XpellaParserBookmark(this.inputStream));
			
			// NOTE Theorically this shouldn't happen, because scope is masked by function's one
			// } else if (this.variableExists(name)) {
			//   this.inputStream.throw('XP041D: Variable "' + name + '" is already declared in this scope', name.length);
			// }
			
			this.lexer.skipWhitespaces(); // Eat whitespaces
			
			let initialAssignation = null;
			if (this.inputStream.peek() === '=') {
				// User wants to have a default value
				this.lexer.eatChar('=');
				initialAssignation = this.parseExpression();
				this.lexer.skipWhitespaces(false); // Eat whitespaces
			}
			
			// Can't have a non default parameter after a default one
			if (!initialAssignation && list.length && list[list.length - 1].initialAssignation) {
				this.inputStream.throw('XP0421: Cannot have a non default parameter after a default one');
			}
			
			list.push(new XpellaASTVariableDeclaration([], '', name, type, 'public', [], initialAssignation));
			
			if (this.inputStream.peek() === ',') {
				this.lexer.eatChar(',');
			}
			
			this.lexer.skipWhitespaces(false); // Eat whitespaces
		}
		
		this.lexer.skipWhitespaces(false); // Eat whitespaces
		this.lexer.eatChar(')');
		
		return list;
	}
	
	public XpellaASTStatement parseStatement(boolean createScopeIfNeeded)
			throws UnexpectedCharacterException, MethodNotFoundException, WrongMethodArgumentsException, NoThisException,
			NoDeclaredOperatorException, StaticCallException, MemberNotFoundException, NoMethodReturnTypeException,
			UnmetExpectationException, WrongOperatorArgumentTypeException, ScopeException, TypeNotFoundException,
			NotAFunctionException
	{
		this.lexer.skipWhitespaces(); // Eat whitespaces
	
		int initialPosition = this.inputStream.getCurrentPosition();
		String currentWord = this.lexer.readWord();
		this.lexer.skipWhitespaces(); // Eat whitespaces
		
		// TODO Add remaining constructs
		if(currentWord.equals("if"))
		{
			// Rewind and parse the condition
			this.inputStream.rewind(initialPosition);
			return this.parseCondition();
		}
		else if(currentWord.equals("continue")|| currentWord.equals("break"))
		{
			// TODO Check if we are in a loop
			this.lexer.skipWhitespaces(); // Eat whitespaces
			this.lexer.eatChar(XpellaParserRules.LINE_DELIMITER);
		}
		else if (currentWord.equals("return"))
		{
			XpellaASTExpression expression = null;
			if (!this.currentFunctionExpectedReturnType.equals("void"))
			{
				int expressionPosition = this.inputStream.getCurrentPosition();
				expression = this.parseExpression(0);
				// TODO Check if type is assignable from
				if (!expression.getResolvedType().equals(this.currentFunctionExpectedReturnType)) {
					this.inputStream.rewind(expressionPosition);
					this.inputStream.throw('XP041E: Expected type "' + this.currentFunctionExpectedReturnType
							+ '" (from method return type), got "' + expression.resolvedType + '"');
				}
			}
			this.lexer.skipWhitespaces(); // Eat whitespaces
			this.lexer.eatChar(XpellaParserRules.LINE_DELIMITER);
			return new XpellaASTReturnStatement(expression);
		}
		else if(currentWord.isEmpty())
		{
			if (this.inputStream.peek().equals("{"))
				// No need to rewind as we didn't advanced the stream
				return this.parseBlock(createScopeIfNeeded);
			else if (this.inputStream.peek().equals(XpellaParserRules.LINE_DELIMITER))
			{
				// Just a ";" with no expression, eat it and return "no statement"
				this.inputStream.next();
				return null;
			}
		}
		else
		{
			this.inputStream.rewind(initialPosition);
			return this.parseExpressionStatement();
		}
		
		return null;
	}
	
	// Not required to be a simple expression, can also be a variable declaration and/or assignation
	public XpellaASTStatement parseExpressionStatement()
	{
	this.lexer.skipWhitespaces(false); // Eat whitespaces

    const initialPosition = this.inputStream.getCurrentPosition();
    const firstWord = this.lexer.readWord();
	this.lexer.skipWhitespaces(false); // Eat whitespaces
	// We have a word, but it does not corresponds to a language keyword, so it can be
	// 1. a variable declaration : [type] [identifier] ([operator] [expression]);
	// 2. an assignation : [identifier] [operator] [expression];
	// 3. a plain expression : [expression];
	
	// Try to read a second word, then try to read an operator
    const ipos = XpellaParserBookmark.fromInputStream(this.inputStream);
    const possibleIdentifier = this.lexer.readWord();
	
	this.lexer.skipWhitespaces(false); // Eat whitespaces
	let operatorPosition = this.inputStream.getCurrentPosition();
	let possibleOperator = this.lexer.readOperator();
	
	this.lexer.skipWhitespaces(false); // Eat whitespaces
	
	let statement: XpellaASTStatement;
	
	let preresolvedLHS: XpellaASTExpression = null;
	
	if (this.inputStream.peek() === XpellaParserObjectAccessor && !possibleIdentifier) {
		// Well, we face an object call at first, resolve it
		this.inputStream.rewind(initialPosition);
      const callee = this.resolveVariableOrLiteral();
		preresolvedLHS = this.maybeObjectCall(callee);
		
		// Then find out if we have a statement operator
		this.lexer.skipWhitespaces(false); // Eat whitespaces
		operatorPosition = this.inputStream.getCurrentPosition();
		possibleOperator = this.lexer.readOperator();
	}

    const hasOperator: boolean = !!(possibleOperator && this.lexer.isStatementOperator(possibleOperator));
    const hasIdentifier: boolean = !!possibleIdentifier;
    const isDeclaration: boolean = hasIdentifier
			&& (hasOperator || this.inputStream.peek() === XpellaParserLineDelimiter);
	
	if (hasOperator && preresolvedLHS instanceof XpellaASTObjectCallMethod) {
		this.inputStream.throw('XP041F: Cannot assign a value to a method return', possibleOperator.length);
	}
	
	if (!isDeclaration && !hasOperator) {
		// This must be a plain expression
		this.inputStream.rewind(initialPosition);
		if (preresolvedLHS) {
			statement = this.solveExpression(preresolvedLHS);
		} else {
			statement = this.parseExpression();
		}
	} else if (isDeclaration) {
		// This is a declaration (with or without assignment)
		// No need to check for a pre resolved call, this can't happen
		
		// Try to get type, this throws if it does not exists, so it also acts like a check
      const type = this.getType(firstWord);
		
		// Check identifier in case it is a reserved keyword
		this.checkReserved(possibleIdentifier, ipos.line, ipos.column);
		
		// Check if variable exists
		if (this.variableExists(possibleIdentifier)) {
			this.inputStream.rewind(ipos.position);
			this.inputStream.throw('XP0401: Variable "' + possibleIdentifier + '" is already declared in this scope');
		}
		
		let expression = null;
		if (hasOperator) {
			// This is when we have a declaration with an assignment
			
			// Only assign operator is accepted on declaration
			if (possibleOperator !== '=') {
				this.inputStream.rewind(operatorPosition);
				this.inputStream.throw('XP0402: Cannot use a complex assigment on a variable declaration, '
						+ 'only simple assignment operator "=" is accepted');
			}
			
			// Parse the assignment expression
        const expressionPosition = this.inputStream.getCurrentPosition();
			expression = this.parseExpression();
			
			// Type check time !! If types are the same, no need to check
			if (expression.resolvedType !== firstWord
					&& (!type.operators['='] || !type.operators['='][expression.resolvedType])) {
				this.inputStream.rewind(expressionPosition);
				this.inputStream.throw('XP0403: Cannot assign expression of type "' + expression.resolvedType
						+ '" to variable of type "' + firstWord + '"');
			}
		}
		
		// TODO Modifiers
		statement = new XpellaASTVariableDeclaration([], '', possibleIdentifier, firstWord, 'public', [], expression);
		this.createVariable(new XpellaParserMemberHeader(possibleIdentifier, firstWord, 'public', []));
	} else {
		// We have a variable assignation
		let type;
		let identifier;
		let isConst = false;
		if (preresolvedLHS) {
        const typedPreresolvedLHS = preresolvedLHS as XpellaASTObjectCallMember;
			type = typedPreresolvedLHS.resolvedType;
			isConst = this.getMemberInType(typedPreresolvedLHS.object.resolvedType,
					typedPreresolvedLHS.member.identifier)
					.modifiers.some((id) => id === 'const');
		} else {
        const variable = this.getVariable(firstWord);
			isConst = variable.modifiers.some((id) => id === 'const');
			type = variable.type;
			identifier = identifier;
		}
		
		if (isConst) {
			this.inputStream.throw('XP0420: Cannot assign a value to a const variable');
		}

      const variableType = this.getType(type);
		let baseOperator: string;
		// Verify that assignation operator is supported
		if (possibleOperator.length > 1) { // Complex operator, need to check, otherwise it is a simple assignation
			baseOperator = possibleOperator.slice(0, possibleOperator.length - 1);
			if (!variableType.operators[baseOperator]) {
				this.inputStream.rewind(operatorPosition);
				this.inputStream.throw('XP0404: Cannot use  assignment operator "' + possibleOperator
						+ '" on type "' + type + '" : base operator "' + baseOperator + '" is not defined on type');
			}
		}
		
		// Parse the assignment expression
      const expressionPosition = this.inputStream.getCurrentPosition();
		let assignmentExpression = this.parseExpression();
		
		// If this is a complex operator, left hand must support the operation with the right hand type
		if (baseOperator) {
			if (!variableType.operators[baseOperator][assignmentExpression.resolvedType]) {
				this.inputStream.rewind(operatorPosition);
				this.inputStream.throw('XP0405: Operator "' + baseOperator + '" from type "' + type
						+ '" cannot accept type "' + assignmentExpression.resolvedType + '"');
			} else {
				// Add the operator to the expression
				assignmentExpression = new XpellaASTExpressionOperator(
						[], '',
						type,
						baseOperator,
						preresolvedLHS ? preresolvedLHS : new XpellaASTVariable([], '', type, identifier),
				assignmentExpression);
			}
		}
		
		// Check assignment type, if types are the same, no need to check
		if (assignmentExpression.resolvedType !== type
				&& (!variableType.operators['='] || !variableType.operators['='][assignmentExpression.resolvedType])) {
			this.inputStream.rewind(expressionPosition);
			this.inputStream.throw('XP0406: Cannot assign expression of type "' + assignmentExpression.resolvedType
					+ '" to variable of type "' + type + '"');
		}
		
		statement = new XpellaASTAssignment(
				[], '',
				preresolvedLHS ? preresolvedLHS as XpellaASTObjectCallMember : new XpellaASTVariable([], '', type, identifier),
		assignmentExpression);
	}
	
	this.lexer.skipWhitespaces(false); // Eat whitespaces
	this.lexer.eatChar(XpellaParserLineDelimiter);
	
	return statement;
}
	
	public XpellaASTStatement parseCondition()
			throws UnexpectedCharacterException, MethodNotFoundException, ScopeException, NotAFunctionException,
			WrongMethodArgumentsException, WrongOperatorArgumentTypeException, StaticCallException, NoThisException,
			NoDeclaredOperatorException, MemberNotFoundException, NoMethodReturnTypeException, TypeNotFoundException,
			UnmetExpectationException
	{
		// If/else
		this.lexer.skipWhitespaces();
		// Eat if keyword
		this.lexer.eatWord("if");
		
		this.lexer.skipWhitespaces(); // Eat whitespaces
		this.lexer.eatChar("(");
		this.lexer.skipWhitespaces(); // Eat whitespaces
		int expressionPosition = this.inputStream.getCurrentPosition();
		if (this.inputStream.peek().equals(")"))
			this.inputStream.throw('XP0407: Expected condition expression');
		
		// Parse expression in parenthesis
		XpellaASTExpression condition = this.parseExpression(0);
		if(!condition.getResolvedType().equals("boolean"))
		{
			this.inputStream.rewind(expressionPosition);
			this.inputStream.throw('XP0408: Expected expression to resolve to a boolean');
		}
		
		this.lexer.skipWhitespaces(); // Eat whitespaces
		this.lexer.eatChar(")");
		this.lexer.skipWhitespaces(); // Eat whitespaces
		
		XpellaParserBookmark passConditionBookmark = new XpellaParserBookmark(this.inputStream);
		this.scopeManager.createChildScope(new ArrayList<>());
		XpellaASTStatement passConditionExecution = this.optimizeScopedStatement(this.parseStatement(false), passConditionBookmark);
		this.scopeManager.removeChildScope();
		
		XpellaASTStatement failConditionExecution = null;
		
		this.lexer.skipWhitespaces(); // Eat whitespaces
		// Maybe we have a else after
		int currentPosition = this.inputStream.getCurrentPosition(); // To rewind in case there is no else
		String nextWord = this.lexer.readWord();
		if (nextWord.equals("else"))
		{
			// We have a else
			this.lexer.skipWhitespaces(false); // Eat whitespaces
			XpellaParserBookmark failConditionBookmark = new XpellaParserBookmark(this.inputStream);
			this.scopeManager.createChildScope(new ArrayList<>());
			failConditionExecution = this.optimizeScopedStatement(this.parseStatement(false), failConditionBookmark);
			this.scopeManager.removeChildScope();
		}
		else
			this.inputStream.rewind(currentPosition);
		
		// Don't return a block if we have nothing to execute
		if(passConditionExecution == null && failConditionExecution == null)
			return null;
		else if(condition == null && failConditionExecution == null)
			return null;
		else if(condition instanceof XpellaASTLiteral)
		{
			XpellaASTLiteral literalCondition = (XpellaASTLiteral)condition;
			if (literalCondition.getResolvedType().equals("boolean")) {
				if(literalCondition.getValue().equals(true))
					return passConditionExecution;
				else
					return failConditionExecution;
			}
		}
		else if(passConditionExecution != null && passConditionExecution.equals(failConditionExecution))
			return passConditionExecution;
		
		return new XpellaASTCondition(condition, passConditionExecution, failConditionExecution);
	}
	
	public XpellaASTStatement parseBlock(boolean createScope)
			throws UnexpectedCharacterException, ScopeException, MethodNotFoundException, NotAFunctionException,
			WrongMethodArgumentsException, WrongOperatorArgumentTypeException, UnmetExpectationException, NoThisException,
			MemberNotFoundException, NoMethodReturnTypeException, StaticCallException, TypeNotFoundException,
			NoDeclaredOperatorException
	{
		// Eat whitespaces
		this.lexer.skipWhitespaces();
		// Expect a bracket
		this.lexer.eatChar("{");
	
		List<XpellaASTStatement> statements = new ArrayList<>();
		
		if(createScope)
			this.scopeManager.createChildScope(new ArrayList<>());
		
		while (!this.inputStream.peek().equals("}"))
		{
		  	XpellaASTStatement statement = this.parseStatement(true);
			if(statement != null)
				statements.add(statement);
			
			// Eat whitespaces
			this.lexer.skipWhitespaces();
		}
		
		this.lexer.eatChar("}");
		
		this.scopeManager.removeChildScope();
		
		if(statements.isEmpty())
			return null;
		else if(statements.size() == 1)
			return statements.get(0);
		
		return new XpellaASTBlock(statements);
	}
	
	public XpellaASTStatement optimizeScopedStatement(XpellaASTStatement statement, XpellaParserBookmark bookmark)
	{
		if(statement instanceof XpellaASTDeclaration)
		{
			this.addWarning("Useless declaration in single statement block", bookmark);
			return null;
		}
		else
			return statement;
	}
	
	/**
	 * Tests if an identifier does not conflict with language constructs. DOES NOT TEST IF IDENTIFIER IS ALREADY TAKEN.
	 * Throws an error if identifier is reserved.
	 * @param identifier The identifier to test
	 */
	public void checkReserved(String identifier, XpellaParserBookmark bookmark) throws NameReservedException
	{
		if(identifier.equals(XpellaParserRules.THIS_KEYWORD) || identifier.equals(XpellaParserRules.TYPE_INSTANTIATION_KEYWORD))
		{
			this.inputStream.rewind(this.inputStream.getCurrentPosition() - identifier.length());
			throw new NameReservedException(identifier, "", bookmark);
		}
		else if(this.defaultTypes.stream().anyMatch(type -> type.getIdentifier().equals(identifier)) ||
			this.declaredTypes.stream().anyMatch(type -> type.getIdentifier().equals(identifier)))
		{
			this.inputStream.rewind(this.inputStream.getCurrentPosition() - identifier.length());
			this.inputStream.throw('XP040B: "' + identifier + '" is a declared type, name conflicts with type object');
		}
		else if(XpellaParserRules.STATEMENT_KEYWORDS.contains(identifier))
		{
			this.inputStream.rewind(this.inputStream.getCurrentPosition() - identifier.length());
			throw new NameReservedException(identifier, "statement", bookmark);
		}
		else if(XpellaParserRules.EXPRESSION_KEYWORDS.contains(identifier))
		{
			this.inputStream.rewind(this.inputStream.getCurrentPosition() - identifier.length());
			throw new NameReservedException(identifier, "expression", bookmark);
		}
		else if(identifier.equals(XpellaParserRules.TYPE_DECLARATION_KEYWORD))
			this.addWarning("\"" + identifier + "\": Identifier could be confused with the eponym type declaration keyword", bookmark);
		else if(XpellaParserRules.VISIBILITIES.contains(identifier))
			this.addWarning("\"" + identifier + "\": Identifier could be confused with the eponym visibility keyword", bookmark);
		else if(XpellaParserRules.MODIFIERS.contains(identifier))
			this.addWarning("\"" + identifier + "\": Identifier could be confused with the eponym modifier keyword", bookmark);
		else if(XpellaParserRules.CONSTRUCTS.contains(identifier))
			this.addWarning("\"" + identifier + "\": Identifier could be confused with the eponym construct keyword", bookmark);
	}
}
