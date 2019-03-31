package io.github.goveranter.xpella.parser.implementation;

import io.github.goveranter.xpella.ast.XpellaASTExpression;
import io.github.goveranter.xpella.ast.XpellaASTLiteral;
import io.github.goveranter.xpella.ast.XpellaASTStaticType;
import io.github.goveranter.xpella.ast.XpellaASTVariable;
import io.github.goveranter.xpella.exceptions.parser.NoThisException;
import io.github.goveranter.xpella.exceptions.parser.UnexpectedCharacterException;
import io.github.goveranter.xpella.exceptions.parser.UnmetExpectationException;
import io.github.goveranter.xpella.exceptions.scopes.ScopeException;
import io.github.goveranter.xpella.parser.XpellaParserRules;
import io.github.goveranter.xpella.parser.headers.XpellaParserVariableHeader;
import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

import java.util.regex.Pattern;

public abstract class XpellaAbstractVariableParser extends XpellaAbstractParser
{
	public XpellaAbstractVariableParser(String sourceCode)
	{
		super(sourceCode);
	}
	
	/** Tries to match a literal (either int, float, string or boolean) or a variable name */
	protected XpellaASTExpression resolveVariableOrLiteral() throws UnmetExpectationException, NoThisException, ScopeException, UnexpectedCharacterException
	{
		// Eat whitespaces
		this.lexer.skipWhitespaces();
		
		if(XpellaParserRules.STRING_DELIMITERS.contains(this.inputStream.peek()))
			return this.resolveStringLiteral();
		else if(this.inputStream.peek().equals("."))
		{
			// This may be a decimal separator
			int initialPos = this.inputStream.getCurrentPosition();
			this.inputStream.next();
			String potentialNumber = this.lexer.readWord();
			this.inputStream.rewind(initialPos);
			if(Pattern.matches("\\d+", potentialNumber))
				return this.resolveNumberLiteral(); // Definitely a floating number
			else // This should be an object call without object identifier
				throw new UnmetExpectationException("Expected object identifier", new XpellaParserBookmark(this.inputStream));
		}
		
		int initialPosition = this.inputStream.getCurrentPosition();
		String word = this.lexer.readWord();
		
		if(word.isEmpty())
			throw new UnmetExpectationException("Expected variable name or literal value", new XpellaParserBookmark(this.inputStream));
		
		if(word.equals("null")) // The null value
			return new XpellaASTLiteral("null", null);
		else if(word.equals("true")) // This is the boolean value
			return new XpellaASTLiteral("boolean", true);
		else if(word.equals("false")) // This is the boolean value
			return new XpellaASTLiteral("boolean", false);
		else if(word.equals(XpellaParserRules.THIS_KEYWORD))
		{
			if(this.currentThis == null)
			{
				this.inputStream.rewind(initialPosition);
				throw new NoThisException(new XpellaParserBookmark(this.inputStream));
			}
			return new XpellaASTVariable(this.currentThis.getIdentifier(), "this");
		}
		else if(Pattern.matches("\\d", word.substring(0, 1)))
		{
			// This is a number literal
			this.inputStream.rewind(initialPosition);
			return this.resolveNumberLiteral();
		}
		else
		{
			// If the type exists, then this is a type
			if(this.typeExists(word))
				return new XpellaASTStaticType(word);
			else
			{
				XpellaParserVariableHeader variable = this.scopeManager.getMemoryValue(word);
				// Must be a variable name
				return new XpellaASTVariable(variable.getType(), word);
			}
		}
	}
	
	/** Resolves a string in its delimiter */
	protected XpellaASTLiteral resolveStringLiteral() throws UnmetExpectationException, UnexpectedCharacterException
	{
		// Eat whitespaces
		this.lexer.skipWhitespaces();
		
		String delimiter = this.inputStream.peek();
		if(XpellaParserRules.STRING_DELIMITERS.contains(delimiter))
			throw new UnmetExpectationException("Expected \"" + delimiter + "\" to be a literal string delimiter", new XpellaParserBookmark(this.inputStream));
		this.inputStream.next();
		
		String str = this.lexer.readWhile((next, current) ->
				!next.equals(delimiter) || current.equals(XpellaParserRules.STRING_ESCAPE));
		
		this.lexer.eatChar(delimiter);
		
		return new XpellaASTLiteral("string", str);
	}
	
	/** Resolves a number (either a int or a float) */
	protected XpellaASTLiteral resolveNumberLiteral() throws UnmetExpectationException
	{
		// Numbers can either be 0.0 or .0
		// Eat whitespaces
		this.lexer.skipWhitespaces();
		
		if(this.inputStream.peek().equals("."))
		{
			// This may be a decimal separator
			this.inputStream.next();
			int initialPos = this.inputStream.getCurrentPosition();
			String word = this.lexer.readWord();
			
			if(Pattern.matches("\\d+", word)) // Definitely a floating number
				return new XpellaASTLiteral("float", Float.parseFloat("0." + word));
			else
			{
				// This is not what we expected
				this.inputStream.rewind(initialPos);
				throw new UnmetExpectationException("Expected floating number", new XpellaParserBookmark(this.inputStream));
			}
		}
		
		int initialPosition = this.inputStream.getCurrentPosition();
		
		// Read the first group
		String num = this.lexer.readWord();
		
		if(!Pattern.matches("\\d+", num))
		{
			this.inputStream.rewind(initialPosition);
			throw new UnmetExpectationException("Expected \"" + num + "\" to be a number (int or float)", new XpellaParserBookmark(this.inputStream));
		}
		
		// Check if it is a decimal
		if(this.inputStream.peek().equals("."))
		{
			// Read decimal part
			this.inputStream.next();
			String dec = this.lexer.readWord();
			
			if(Pattern.matches("\\d+", dec))
				return new XpellaASTLiteral("float", Float.parseFloat(num + "." + dec));
			else
			{
				this.inputStream.rewind(initialPosition);
				throw new UnmetExpectationException("Expected \"" + num + "." + dec + "\" to be a float", new XpellaParserBookmark(this.inputStream));
			}
		}
		else
			return new XpellaASTLiteral("int", Integer.parseInt(num));
	}
}
