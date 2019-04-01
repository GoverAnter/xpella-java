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
			MethodNotFoundException, WrongMethodArgumentsException, TypeCoercionException, DefaultArgumentOrderException,
			NoDeclaredOperatorException, MemberNotFoundException, NoMethodReturnTypeException, StaticCallException,
			TypeNotFoundException, NotAFunctionException, NameReservedException, ParserVariableAlreadyDeclaredException,
			CannotAssignValueException, VariableAssignmentException, MemberAlreadyDeclared, UndefinedOperatorException,
			ComplexAssignmentException, InvalidModifierException, MultipleVisibilityModifiersException, ScopeException,
			NameConflictException
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
			MethodNotFoundException, WrongMethodArgumentsException, TypeCoercionException, DefaultArgumentOrderException,
			NoDeclaredOperatorException, MemberNotFoundException, NoMethodReturnTypeException, StaticCallException,
			TypeNotFoundException, NotAFunctionException, NameReservedException, ParserVariableAlreadyDeclaredException,
			CannotAssignValueException, VariableAssignmentException, MemberAlreadyDeclared, UndefinedOperatorException,
			ComplexAssignmentException, InvalidModifierException, MultipleVisibilityModifiersException, ScopeException,
			NameConflictException
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
		
		this.lexer.skipWhitespaces(); // Eat whitespaces
		this.lexer.eatChar("{");
		this.lexer.skipWhitespaces(); // Eat whitespaces
		
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
			
			String name = null;
			String type = null;
			String visibility = null;
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
						throw new MemberAlreadyDeclared(currentWord, false, new XpellaParserBookmark(this.inputStream));
					}
					if(this.currentThis.getMembers().stream().anyMatch(mem -> mem.getIdentifier().equals(currentWord)))
					{
						this.inputStream.rewind(positions.get(i).getPosition());
						throw new MemberAlreadyDeclared(currentWord, true, new XpellaParserBookmark(this.inputStream));
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
							throw new MultipleVisibilityModifiersException(new XpellaParserBookmark(this.inputStream));
						}
						visibility = currentWord;
					}
					else
					{
						// Should be a modifier
						if(!XpellaParserRules.MODIFIERS.contains(currentWord))
						{
							this.inputStream.rewind(positions.get(i).getPosition());
							throw new InvalidModifierException(currentWord, new XpellaParserBookmark(this.inputStream));
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
						args, this.parseBlock(true)));
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
			
			this.lexer.skipWhitespaces(); // Eat whitespaces
		}
		
		this.lexer.eatChar("}");
	
		XpellaASTTypeDeclaration typeDeclaration = this.currentThis;
		this.currentThis = null; // Set it just in case
		this.declaredTypes.add(XpellaParserTypeHeader.fromDeclaration(typeDeclaration));
		return typeDeclaration;
	}
	
	public List<XpellaASTVariableDeclaration> parseSimpleVariableDeclarationList(String currentType)
			throws UnmetExpectationException, UnexpectedCharacterException, NoThisException, NotAFunctionException,
			MethodNotFoundException, WrongMethodArgumentsException, TypeCoercionException, ScopeException,
			NoDeclaredOperatorException, MemberNotFoundException, NoMethodReturnTypeException, StaticCallException,
			TypeNotFoundException, DefaultArgumentOrderException
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
				throw new TypeNotFoundException(type, new XpellaParserBookmark(this.inputStream));
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
			
			XpellaASTExpression initialAssignation = null;
			if (this.inputStream.peek().equals("=")) {
				// User wants to have a default value
				this.lexer.eatChar("=");
				initialAssignation = this.parseExpression(0);
				this.lexer.skipWhitespaces(); // Eat whitespaces
			}
			
			// Can't have a non default parameter after a default one
			if(initialAssignation == null && list.size() > 0 && list.get(list.size() - 1).getInitialAssignation() != null)
				throw new DefaultArgumentOrderException(new XpellaParserBookmark(this.inputStream));
			
			list.add(new XpellaASTVariableDeclaration(name, type, "public", new ArrayList<>(), initialAssignation));
			
			if(this.inputStream.peek().equals(","))
				this.lexer.eatChar(",");
			
			this.lexer.skipWhitespaces(); // Eat whitespaces
		}
		
		this.lexer.skipWhitespaces(); // Eat whitespaces
		this.lexer.eatChar(")");
		
		return list;
	}
	
	public XpellaASTStatement parseStatement(boolean createScopeIfNeeded)
			throws UnmetExpectationException, UnexpectedCharacterException, NoThisException, VariableAssignmentException,
			MethodNotFoundException, WrongMethodArgumentsException, TypeCoercionException, ScopeException,
			NoDeclaredOperatorException, MemberNotFoundException, NoMethodReturnTypeException, StaticCallException,
			TypeNotFoundException, NotAFunctionException, NameReservedException, ParserVariableAlreadyDeclaredException,
			CannotAssignValueException, UndefinedOperatorException, ComplexAssignmentException, NameConflictException
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
				if (!expression.getResolvedType().equals(this.currentFunctionExpectedReturnType))
				{
					this.inputStream.rewind(expressionPosition);
					throw new UnmetExpectationException("Expected type \"" + this.currentFunctionExpectedReturnType +
							"\" (from method return type), got \"" + expression.getResolvedType() + "\"",
							new XpellaParserBookmark(this.inputStream));
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
			throws UnmetExpectationException, UnexpectedCharacterException, NoThisException, VariableAssignmentException,
			MethodNotFoundException, WrongMethodArgumentsException, TypeCoercionException, ScopeException,
			NoDeclaredOperatorException, MemberNotFoundException, NoMethodReturnTypeException, StaticCallException,
			TypeNotFoundException, NotAFunctionException, NameReservedException, ParserVariableAlreadyDeclaredException,
			CannotAssignValueException, UndefinedOperatorException, ComplexAssignmentException, NameConflictException
	{
		this.lexer.skipWhitespaces(); // Eat whitespaces
	
		int initialPosition = this.inputStream.getCurrentPosition();
		String firstWord = this.lexer.readWord();
		this.lexer.skipWhitespaces(); // Eat whitespaces
		// We have a word, but it does not corresponds to a language keyword, so it can be
		// 1. a variable declaration : [type] [identifier] ([operator] [expression]);
		// 2. an assignation : [identifier] [operator] [expression];
		// 3. a plain expression : [expression];
		
		// Try to read a second word, then try to read an operator
		XpellaParserBookmark ipos = new XpellaParserBookmark(this.inputStream);
		String possibleIdentifier = this.lexer.readWord();
		
		this.lexer.skipWhitespaces(); // Eat whitespaces
		int operatorPosition = this.inputStream.getCurrentPosition();
		String possibleOperator = this.lexer.readOperator();
		
		this.lexer.skipWhitespaces(); // Eat whitespaces
		
		XpellaASTStatement statement;
		XpellaASTObjectCall preresolvedLHS = null;
		
		if (this.inputStream.peek().equals(XpellaParserRules.OBJECT_ACCESSOR) && possibleIdentifier.isEmpty()) {
			// Well, we face an object call at first, resolve it
			this.inputStream.rewind(initialPosition);
		  	XpellaASTExpression callee = this.resolveVariableOrLiteral();
			preresolvedLHS = this.maybeObjectCall(callee).orElse(null);
			
			// Then find out if we have a statement operator
			this.lexer.skipWhitespaces(); // Eat whitespaces
			operatorPosition = this.inputStream.getCurrentPosition();
			possibleOperator = this.lexer.readOperator();
		}
	
		boolean hasOperator = !possibleOperator.isEmpty() && this.lexer.isStatementOperator(possibleOperator);
		boolean hasIdentifier = !possibleIdentifier.isEmpty();
		boolean isDeclaration = hasIdentifier && (hasOperator || this.inputStream.peek().equals(XpellaParserRules.LINE_DELIMITER));
		
		if(hasOperator && preresolvedLHS instanceof XpellaASTObjectCallMethod)
		{
			this.inputStream.rewind(this.inputStream.getCurrentPosition() - possibleOperator.length());
			throw new CannotAssignValueException("method call", new XpellaParserBookmark(this.inputStream));
		}
		
		if(!isDeclaration && !hasOperator)
		{
			// This must be a plain expression
			this.inputStream.rewind(initialPosition);
			if(preresolvedLHS != null)
				statement = this.solveExpression(preresolvedLHS, 0);
			else
				statement = this.parseExpression(0);
		}
		else if(isDeclaration)
		{
			// This is a declaration (with or without assignment)
			// No need to check for a pre resolved call, this can't happen
			
			// Try to get type, this throws if it does not exists, so it also acts like a check
		  	XpellaParserTypeHeader type = this.getType(firstWord);
			
			// Check identifier in case it is a reserved keyword
			this.checkReserved(possibleIdentifier, ipos);
			
			// Check if variable exists
			if(this.scopeManager.exists(possibleIdentifier))
			{
				this.inputStream.rewind(ipos.getPosition());
				throw new ParserVariableAlreadyDeclaredException(possibleIdentifier, new XpellaParserBookmark(this.inputStream));
			}
			
			XpellaASTExpression expression = null;
			if (hasOperator)
			{
				// This is when we have a declaration with an assignment
				
				// Only assign operator is accepted on declaration
				if(!possibleOperator.equals("="))
				{
					this.inputStream.rewind(operatorPosition);
					throw new ComplexAssignmentException(new XpellaParserBookmark(this.inputStream));
				}
				
				// Parse the assignment expression
				int expressionPosition = this.inputStream.getCurrentPosition();
				expression = this.parseExpression(0);
				
				// Type check time !! If types are the same, no need to check
				checkVariableAssignment(expression, type, expressionPosition);
			}
			
			// TODO Modifiers
			statement = new XpellaASTVariableDeclaration(possibleIdentifier, firstWord, "public", new ArrayList<>(), expression);
			this.scopeManager.declareMemoryValue(new XpellaParserVariableHeader(possibleIdentifier, firstWord, "public", new ArrayList<>()));
		} else {
			// We have a variable assignation
			String type;
			String identifier = null;
			boolean isConst = false;
			if(preresolvedLHS != null)
			{
				XpellaASTObjectCallMember typedPreresolvedLHS = (XpellaASTObjectCallMember)preresolvedLHS;
				type = typedPreresolvedLHS.getResolvedType();
				isConst = this.getMemberInType(typedPreresolvedLHS.getObject().getResolvedType(),
								typedPreresolvedLHS.getMember().getIdentifier())
						.getModifiers().stream().anyMatch(id -> id.equals("const"));
			}
			else
			{
				XpellaParserVariableHeader variable = this.scopeManager.getMemoryValue(firstWord);
				isConst = variable.getModifiers().stream().anyMatch(id -> id.equals("const"));
				type = variable.getType();
				identifier = firstWord;
			}
			
			if(isConst)
				throw new CannotAssignValueException("const variable", new XpellaParserBookmark(this.inputStream));
	
		  	XpellaParserTypeHeader variableType = this.getType(type);
			String baseOperator = "";
			// Verify that assignation operator is supported
			if(possibleOperator.length() > 1)
			{ // Complex operator, need to check, otherwise it is a simple assignation
				baseOperator = possibleOperator.substring(0, possibleOperator.length() - 1);
				if(!variableType.getOperators().containsKey(baseOperator))
				{
					this.inputStream.rewind(operatorPosition);
					throw new UndefinedOperatorException("Cannot use assignment operator \"" + possibleOperator +
							"\" on type \"" + type + "\" : base operator \"" + baseOperator + "\" is not defined on type",
							new XpellaParserBookmark(this.inputStream));
				}
			}
			
			// Parse the assignment expression
		  	int expressionPosition = this.inputStream.getCurrentPosition();
			XpellaASTExpression assignmentExpression = this.parseExpression(0);
			
			// If this is a complex operator, left hand must support the operation with the right hand type
			if(!baseOperator.isEmpty())
			{
				if (!variableType.getOperators().get(baseOperator).getAcceptedTypes().contains(assignmentExpression.getResolvedType()))
				{
					this.inputStream.rewind(operatorPosition);
					throw new TypeCoercionException(baseOperator, type, assignmentExpression.getResolvedType(),
							new XpellaParserBookmark(this.inputStream));
				}
				else
				{
					// Add the operator to the expression
					assignmentExpression = new XpellaASTExpressionOperator(
							type,
							baseOperator,
							preresolvedLHS != null ? preresolvedLHS : new XpellaASTVariable(type, identifier),
							assignmentExpression);
				}
			}
			
			// Check assignment type, if types are the same, no need to check
			checkVariableAssignment(assignmentExpression, variableType, expressionPosition);
			
			statement = new XpellaASTAssignment(
					preresolvedLHS != null ? (XpellaASTObjectCallMember)preresolvedLHS : new XpellaASTVariable(type, identifier),
					assignmentExpression);
		}
		
		this.lexer.skipWhitespaces(); // Eat whitespaces
		this.lexer.eatChar(XpellaParserRules.LINE_DELIMITER);
		
		return statement;
	}
	
	protected void checkVariableAssignment(XpellaASTExpression expr, XpellaParserTypeHeader variableType, int rewindPos)
			throws VariableAssignmentException
	{
		if(!expr.getResolvedType().equals(variableType.getIdentifier()) &&
				(!variableType.getOperators().containsKey("=") ||
						!variableType.getOperators().get("=").getAcceptedTypes().contains(expr.getResolvedType())))
		{
			this.inputStream.rewind(rewindPos);
			throw new VariableAssignmentException(variableType.getIdentifier(), expr.getResolvedType(),
					new XpellaParserBookmark(this.inputStream));
		}
	}
	
	public XpellaASTStatement parseCondition()
			throws UnmetExpectationException, UnexpectedCharacterException, VariableAssignmentException, NoThisException,
			MethodNotFoundException, WrongMethodArgumentsException, TypeCoercionException, ScopeException,
			NoDeclaredOperatorException, MemberNotFoundException, NoMethodReturnTypeException, StaticCallException,
			TypeNotFoundException, NotAFunctionException, NameReservedException, ParserVariableAlreadyDeclaredException,
			CannotAssignValueException, UndefinedOperatorException, ComplexAssignmentException, NameConflictException
	{
		// If/else
		this.lexer.skipWhitespaces();
		// Eat if keyword
		this.lexer.eatWord("if");
		
		this.lexer.skipWhitespaces(); // Eat whitespaces
		this.lexer.eatChar("(");
		this.lexer.skipWhitespaces(); // Eat whitespaces
		int expressionPosition = this.inputStream.getCurrentPosition();
		
		if(this.inputStream.peek().equals(")"))
			throw new UnmetExpectationException("Expected condition expression", new XpellaParserBookmark(this.inputStream));
		
		// Parse expression in parenthesis
		XpellaASTExpression condition = this.parseExpression(0);
		if(!condition.getResolvedType().equals("boolean"))
		{
			this.inputStream.rewind(expressionPosition);
			throw new UnmetExpectationException("Expected expression to resolve to a boolean", new XpellaParserBookmark(this.inputStream));
		}
		
		this.lexer.skipWhitespaces(); // Eat whitespaces
		this.lexer.eatChar(")");
		this.lexer.skipWhitespaces(); // Eat whitespaces
		
		XpellaParserBookmark passConditionBookmark = new XpellaParserBookmark(this.inputStream);
		this.scopeManager.createChildScope(null);
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
			this.lexer.skipWhitespaces(); // Eat whitespaces
			XpellaParserBookmark failConditionBookmark = new XpellaParserBookmark(this.inputStream);
			this.scopeManager.createChildScope(null);
			failConditionExecution = this.optimizeScopedStatement(this.parseStatement(false), failConditionBookmark);
			this.scopeManager.removeChildScope();
		}
		else
			this.inputStream.rewind(currentPosition);
		
		// Don't return a block if we have nothing to execute
		if(passConditionExecution == null && failConditionExecution == null)
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
			throws UnmetExpectationException, UnexpectedCharacterException, VariableAssignmentException, NoThisException,
			MethodNotFoundException, WrongMethodArgumentsException, TypeCoercionException, ScopeException,
			NoDeclaredOperatorException, MemberNotFoundException, NoMethodReturnTypeException, StaticCallException,
			TypeNotFoundException, NotAFunctionException, NameReservedException, ParserVariableAlreadyDeclaredException,
			CannotAssignValueException, UndefinedOperatorException, ComplexAssignmentException, NameConflictException
	{
		// Eat whitespaces
		this.lexer.skipWhitespaces();
		// Expect a bracket
		this.lexer.eatChar("{");
	
		List<XpellaASTStatement> statements = new ArrayList<>();
		
		if(createScope)
			this.scopeManager.createChildScope(null);
		
		while (!this.inputStream.peek().equals("}"))
		{
		  	XpellaASTStatement statement = this.parseStatement(true);
			if(statement != null)
				statements.add(statement);
			
			// Eat whitespaces
			this.lexer.skipWhitespaces();
		}
		
		this.lexer.eatChar("}");
		
		if(createScope)
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
	public void checkReserved(String identifier, XpellaParserBookmark bookmark)
			throws NameReservedException, NameConflictException
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
			throw new NameConflictException(identifier, bookmark);
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
