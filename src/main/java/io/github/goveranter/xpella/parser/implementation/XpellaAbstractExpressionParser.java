package io.github.goveranter.xpella.parser.implementation;

import io.github.goveranter.xpella.ast.*;
import io.github.goveranter.xpella.exceptions.parser.*;
import io.github.goveranter.xpella.exceptions.scopes.ScopeException;
import io.github.goveranter.xpella.parser.XpellaParserRules;
import io.github.goveranter.xpella.parser.headers.XpellaParserFunctionHeader;
import io.github.goveranter.xpella.parser.headers.XpellaParserMemberHeader;
import io.github.goveranter.xpella.parser.headers.XpellaParserTypeHeader;
import io.github.goveranter.xpella.parser.headers.XpellaParserVariableHeader;
import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class XpellaAbstractExpressionParser extends XpellaAbstractVariableParser
{
	public XpellaAbstractExpressionParser(String sourceCode)
	{
		super(sourceCode);
	}
	
	// 0
	/** This is the main method to parse a complete expression. */
	protected XpellaASTExpression parseExpression(int precedence)
			throws ScopeException, WrongMethodArgumentsException, UnexpectedCharacterException, NoThisException,
			TypeNotFoundException, MemberNotFoundException, NoMethodReturnTypeException, StaticCallException,
			MethodNotFoundException, UnmetExpectationException, NotAFunctionException, NoDeclaredOperatorException,
			WrongOperatorArgumentTypeException
	{
		return this.solveExpression(this.parseNextExpression(false), precedence);
	}
	
	// "(", ")", ","
	protected List<XpellaASTExpression> parseExpressionList()
			throws ScopeException, WrongMethodArgumentsException, UnexpectedCharacterException, NoThisException,
			TypeNotFoundException, MemberNotFoundException, NoMethodReturnTypeException, StaticCallException,
			MethodNotFoundException, UnmetExpectationException, NotAFunctionException, NoDeclaredOperatorException,
			WrongOperatorArgumentTypeException
	{
		this.lexer.skipWhitespaces(); // Eat whitespaces
		this.lexer.eatChar("(");
		this.lexer.skipWhitespaces(); // Eat whitespaces
	
		List<XpellaASTExpression> expressions = new ArrayList<>();
		boolean first = true;
		
		while(!this.inputStream.peek().equals(")"))
		{
			if (first)
				first = false;
			else
				this.lexer.eatChar(",");
			
			expressions.add(this.parseExpression(0));
			
			this.lexer.skipWhitespaces(); // Eat whitespaces
		}
		
		this.lexer.eatChar(")");
		
		return expressions;
	}
	
	// -, 0
	protected XpellaASTExpression solveExpression(XpellaASTExpression lhs, int lastOperatorPrecedence)
			throws ScopeException, WrongMethodArgumentsException, UnexpectedCharacterException, NoThisException,
			TypeNotFoundException, MemberNotFoundException, NoMethodReturnTypeException, StaticCallException,
			MethodNotFoundException, UnmetExpectationException, NotAFunctionException, NoDeclaredOperatorException,
			WrongOperatorArgumentTypeException
	{
		this.lexer.skipWhitespaces(); // Eat whitespaces
		// Peek operator
		int initialPosition = this.inputStream.getCurrentPosition();
		String operator = this.lexer.readOperator();
		
		if(!operator.isEmpty())
		{
			// Get precedence
		  	int precedence = XpellaParserRules.EXPRESSION_OPERATORS.getOrDefault(operator, -1);
			if(precedence == -1)
			{
				String opType = "unknown operator";
				if (XpellaParserRules.STATEMENT_OPERATORS.stream().anyMatch(op -> op.equals(operator)))
					opType = "statement operator";
				
				this.inputStream.rewind(this.inputStream.getCurrentPosition() - operator.length());
				throw new UnmetExpectationException("Expected expression operator, got \"" + operator + "\" " + opType, new XpellaParserBookmark(this.inputStream));
			}
			
			// TODO Resolve literal operations compile time
			if (lastOperatorPrecedence < precedence)
			{
				// If operator is an equality comparison ("==" or "!=") and it is used to compare to the same type
				// Then we can be sure it is present, because it is provided by the language
				boolean isEqualityComparison = operator.equals("==") || operator.equals("!=");
				boolean isComparison = isEqualityComparison || operator.equals("<") || operator.equals(">")
						|| operator .equals("<=") || operator.equals(">=");
	
				XpellaParserTypeHeader lhsType = this.getType(lhs.getResolvedType());
				if (!isEqualityComparison && lhsType.getOperators().get(operator) == null)
				{
					this.inputStream.rewind(this.inputStream.getCurrentPosition() - operator.length());
					throw new NoDeclaredOperatorException(lhs.getResolvedType(), operator, "expression operator", new XpellaParserBookmark(this.inputStream));
				}
				int rhsPosition = this.inputStream.getCurrentPosition();
				XpellaASTExpression rhs = this.parseExpression(precedence);
				if ((!isEqualityComparison || !lhsType.getIdentifier().equals(rhs.getResolvedType()))
						&& !lhsType.getOperators().get(operator).getAcceptedTypes().contains(rhs.getResolvedType()))
				{
					this.inputStream.rewind(rhsPosition);
					throw new WrongOperatorArgumentTypeException(operator, lhs.getResolvedType(), rhs.getResolvedType(),
							new XpellaParserBookmark(this.inputStream));
				}
				return this.solveExpression(
						new XpellaASTExpressionOperator(isComparison ? "boolean" : lhs.getResolvedType(), operator, lhs, rhs),
						lastOperatorPrecedence);
			} else {
				// As we should peek, rewind
				this.inputStream.rewind(initialPosition);
			}
		}
		return lhs;
	}
	// false
	protected XpellaASTExpression parseNextExpression(boolean readForBackOperator)
			throws ScopeException, WrongMethodArgumentsException, UnexpectedCharacterException, NoThisException,
			TypeNotFoundException, MemberNotFoundException, NoMethodReturnTypeException, StaticCallException,
			MethodNotFoundException, UnmetExpectationException, NotAFunctionException, NoDeclaredOperatorException,
			WrongOperatorArgumentTypeException
	{
		this.lexer.skipWhitespaces(); // Eat whitespaces
		
		// If this word is a back operator, and back operator is allowed here
		// parse next expression to complete the expression loop
		if(!readForBackOperator)
		{
		  	String operator = this.lexer.readOperator();
			if(!operator.isEmpty()) {
				if (this.lexer.isBackOperator(operator))
				{
					XpellaASTVariable expr = (XpellaASTVariable)this.parseNextExpression(true);
			  		XpellaParserTypeHeader exprType = this.getType(expr.getResolvedType());
					if (exprType.getOperators().get(operator) == null || !exprType.getOperators().get(operator).isBack())
						throw new NoDeclaredOperatorException(expr.getResolvedType(), operator, "back operator", new XpellaParserBookmark(this.inputStream));
					return new XpellaASTBackOperator(expr.getResolvedType(), operator, expr);
				}
				else
				{
					this.inputStream.rewind(this.inputStream.getCurrentPosition() - operator.length());
					throw new UnmetExpectationException("Expected expression, got operator instead", new XpellaParserBookmark(this.inputStream));
				}
			}
		}
		
		XpellaASTExpression expression;
		
		// Test if this is a string literal, in this case readWord won't help us
		if (XpellaParserRules.STRING_DELIMITERS.contains(this.inputStream.peek()))
		{
			if (readForBackOperator)
				throw new UnmetExpectationException("Expected variable expression for back operator, got string literal instead", new XpellaParserBookmark(this.inputStream));
			else
				expression = this.resolveStringLiteral();
		}
		else if(Pattern.matches("\\d", this.inputStream.peek()))
			// This should be a number (either a float or an int) because an identifier can't begin with a number
			expression = this.resolveNumberLiteral();
		else
		{
		  	int initialPosition = this.inputStream.getCurrentPosition();
		  	String word = this.lexer.readWord();
			
			if(!word.isEmpty())
			{
				// No need to check for object call, we check that later
				// Check if it is a function call, if not then it is a variable/literal
				this.lexer.skipWhitespaces(); // Eat whitespaces
				if (this.inputStream.peek().equals("("))
				{
					// Function call
					this.inputStream.rewind(initialPosition);
					expression = this.resolveFunctionCall(null, false, false);
				}
				else
				{
					if (word.equals(XpellaParserRules.TYPE_INSTANTIATION_KEYWORD))
					{
						// This is an object instantiation, read the type
						this.lexer.skipWhitespaces(); // Eat whitespaces
						int callPosition = this.inputStream.getCurrentPosition();
						String type = this.lexer.readWord();
						
						if(type.isEmpty())
							throw new UnmetExpectationException("Expected type identifier, got \"" + this.inputStream.peek() + "\"",
									new XpellaParserBookmark(this.inputStream));
						
						this.inputStream.rewind(callPosition);
						expression = this.resolveFunctionCall(this.getType(type), false, true);
					}
					else
					{
						this.inputStream.rewind(initialPosition);
						expression = this.resolveVariableOrLiteral();
					}
				}
			}
			else
			{
				if (this.inputStream.peek().equals(".")) // Maybe it is a float, in the form '.12'
					expression = this.resolveNumberLiteral();
				else // Dunno, so throw
					throw new UnmetExpectationException("Expected expression, got \"" + this.inputStream.peek() + "\"",
							new XpellaParserBookmark(this.inputStream));
			}
		}
		
		if(expression == null)
			throw new UnmetExpectationException("Expected expression", new XpellaParserBookmark(this.inputStream));
		else
		{
			if(!readForBackOperator)
			{
				// If needed, check for front operator
				if(expression instanceof XpellaASTVariable)
				{
					Optional<XpellaASTFrontOperator> frontOperator = this.maybeFrontOperator((XpellaASTVariable)expression);
					if(frontOperator.isPresent())
						return frontOperator.get();
				}
				
				// Check for object call right behind this expression
				Optional<XpellaASTObjectCall> objectCall = this.maybeObjectCall(expression);
				if(objectCall.isPresent())
					return objectCall.get();
			}
			
			return expression;
		}
	}
	
	// If there is a front operator, returns a fully constructed object, else return null
	// Does not throw in any case
	protected Optional<XpellaASTFrontOperator> maybeFrontOperator(XpellaASTVariable expression)
			throws TypeNotFoundException, NoDeclaredOperatorException
	{
		this.lexer.skipWhitespaces(); // Eat whitespaces
	
		int operatorPosition = this.inputStream.getCurrentPosition();
		String operator = this.lexer.readOperator();
		if(!operator.isEmpty() && this.lexer.isFrontOperator(operator))
		{
		  	XpellaParserTypeHeader exprType = this.getType(expression.getResolvedType());
			if(exprType.getOperators().get(operator) == null || !exprType.getOperators().get(operator).isFront())
				throw new NoDeclaredOperatorException(expression.getResolvedType(), operator, "front operator",
						new XpellaParserBookmark(this.inputStream));
			return Optional.of(new XpellaASTFrontOperator(expression.getResolvedType(), operator, expression));
		}
		// Rewind just in case we moved
		this.inputStream.rewind(operatorPosition);
		return Optional.empty();
	}
	
	protected Optional<XpellaASTObjectCall> maybeObjectCall(XpellaASTExpression callee)
			throws ScopeException, WrongMethodArgumentsException, UnexpectedCharacterException, NoThisException,
			TypeNotFoundException, MemberNotFoundException, NoMethodReturnTypeException, StaticCallException,
			MethodNotFoundException, UnmetExpectationException, NotAFunctionException, NoDeclaredOperatorException,
			WrongOperatorArgumentTypeException
	{
		this.lexer.skipWhitespaces(); // Eat whitespaces
		if (this.inputStream.peek().equals(XpellaParserRules.OBJECT_ACCESSOR))
		{
			this.inputStream.next(); // Hmmm, maybe, eat the accessor
			this.lexer.skipWhitespaces(); // Eat whitespaces
			int initialPosition = this.inputStream.getCurrentPosition();
			
			// Make sure we have something behind
			String word = this.lexer.readWord();
			if(word.isEmpty())
				throw new UnmetExpectationException("Expected method or member identifier after object accessor",
						new XpellaParserBookmark(this.inputStream));
			
			// Let's see if it is a method call or a member call
			this.lexer.skipWhitespaces(); // Eat whitespaces
			
			XpellaASTObjectCall call;
			XpellaParserTypeHeader calleeType;
			if(callee.getResolvedType().equals(this.currentThis.getIdentifier()))
				calleeType = XpellaParserTypeHeader.fromDeclaration(this.currentThis);
			else
				calleeType = this.getType(callee.getResolvedType());
			
			if (this.inputStream.peek().equals("("))
			{
				// Method call
				this.inputStream.rewind(initialPosition);
				call = new XpellaASTObjectCallMethod(callee, this.resolveFunctionCall(calleeType, callee instanceof XpellaASTStaticType, false));
			} else {
				// Just a member
				Optional<XpellaParserMemberHeader> member = calleeType.getMembers().stream().filter(mem -> mem.getIdentifier().equals(word)).findAny();
				if(member.isEmpty())
				{
					this.inputStream.rewind(this.inputStream.getCurrentPosition() - word.length());
					throw new MemberNotFoundException(word, callee.getResolvedType(), new XpellaParserBookmark(this.inputStream));
				}
				
				XpellaParserMemberHeader memberHeader = member.get();
				
				if (callee instanceof XpellaASTStaticType && memberHeader.getModifiers().stream().noneMatch(mem -> mem.equals("static")))
				{
					this.inputStream.rewind(this.inputStream.getCurrentPosition() - word.length());
					throw new StaticCallException(memberHeader.getIdentifier(), true, new XpellaParserBookmark(this.inputStream));
				}
				call = new XpellaASTObjectCallMember(callee, new XpellaASTVariable(memberHeader.getType(), word));
			}
			// Chain calls, if needed
			return this.maybeObjectCall(call).or(() -> Optional.of(call));
		}
		else
			return Optional.empty(); // Can't be, for sure
	}
	
	// null, false, false
	protected XpellaASTFunctionCall resolveFunctionCall(@Nullable XpellaParserTypeHeader callee, boolean staticCall, boolean objectInstantiation)
			throws ScopeException, WrongMethodArgumentsException, UnexpectedCharacterException, NoThisException,
			TypeNotFoundException, MemberNotFoundException, NoMethodReturnTypeException, StaticCallException,
			MethodNotFoundException, UnmetExpectationException, NotAFunctionException, NoDeclaredOperatorException,
			WrongOperatorArgumentTypeException
	{
		this.lexer.skipWhitespaces(); // Eat whitespaces
		int initialPosition = this.inputStream.getCurrentPosition();
		// Expect function name
		String identifier = this.lexer.readWord();
		
		if(identifier.isEmpty())
			throw new UnmetExpectationException("Expected function identifier for function call, got \"" + this.inputStream.peek() + "\"",
					new XpellaParserBookmark(this.inputStream));
	
		List<XpellaASTExpression> args = this.parseExpressionList();
		List<String> headerArgs = args.stream().map(XpellaASTExpression::getResolvedType).collect(Collectors.toList());
		
		String returnType;
		
		// If callee is null, we're searching a variable, otherwise, we're searching a method in an object
		if (callee == null)
		{
		  	XpellaParserVariableHeader variable = this.scopeManager.getMemoryValue(identifier);
			if(variable instanceof XpellaParserFunctionHeader)
			{
				// Compare args
				XpellaParserFunctionHeader functionHeader = (XpellaParserFunctionHeader)variable;
				if (!this.compareArgsArrays(functionHeader.getArgs(), headerArgs))
				{
					this.inputStream.rewind(initialPosition);
					throw new WrongMethodArgumentsException(functionHeader.getArgs(), headerArgs,
							new XpellaParserBookmark(this.inputStream));
				}
				else // This is it !!
					returnType = functionHeader.getReturnType();
			}
			else
			{
				this.inputStream.rewind(initialPosition);
				throw new NotAFunctionException(identifier, variable.getType(), new XpellaParserBookmark(this.inputStream));
			}
		}
		else
		{
			// So it should be a method call...
			if (objectInstantiation && args.isEmpty()) // Don't try to search it, this is the default constructor
				returnType = callee.getIdentifier();
			else
			{
				// Make sure for method exists on the object
				Optional<XpellaParserFunctionHeader> method = callee.getMethods().stream()
						.filter(meth -> meth.getIdentifier().equals(identifier) && this.compareArgsArrays(meth.getArgs(), headerArgs))
						.findAny();
				
				if(method.isEmpty())
				{
					this.inputStream.rewind(initialPosition);
					throw new MethodNotFoundException(identifier, headerArgs, callee.getIdentifier(),
							new XpellaParserBookmark(this.inputStream));
				}
				else if(staticCall && method.get().getModifiers().stream().noneMatch(mod -> mod.equals("static")))
				{
					this.inputStream.rewind(initialPosition);
					throw new StaticCallException(method.get().getIdentifier(), false,
							new XpellaParserBookmark(this.inputStream));
				}
				else
					returnType = method.get().getReturnType();
			}
		}
		
		if(returnType == null) // Just in case, should never happen
			throw new NoMethodReturnTypeException(identifier, headerArgs, new XpellaParserBookmark(this.inputStream));
		
		return new XpellaASTFunctionCall(returnType, identifier, args);
	}
}
