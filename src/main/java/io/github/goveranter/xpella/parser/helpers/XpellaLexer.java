package io.github.goveranter.xpella.parser.helpers;

import io.github.goveranter.xpella.exceptions.parser.UnexpectedCharacterException;
import io.github.goveranter.xpella.parser.XpellaParserRules;

import java.util.function.BiFunction;
import java.util.regex.Pattern;

public class XpellaLexer
{
	private XpellaInputStream inputStream;
	
	public XpellaLexer(XpellaInputStream inputStream)
	{
		this.inputStream = inputStream;
	}
	
	public boolean isWhitespace(String character)
	{
		return Pattern.matches("\\s", character);
	}
	public boolean isNextWhitespace()
	{
		return this.isWhitespace(this.inputStream.peek());
	}
	
	public boolean isBlockDelimiter(String character)
	{
		return Pattern.matches("[()\\[\\]{}]", character);
	}
	public boolean isNextBlockDelimiter()
	{
		return this.isBlockDelimiter(this.inputStream.peek());
	}
	
	public boolean isLineDelimiter(String character)
	{
		return character.equals(XpellaParserRules.LINE_DELIMITER);
	}
	public boolean isNextLineDelimiter()
	{
		return this.isLineDelimiter(this.inputStream.peek());
	}
	
	public boolean isSimpleDelimiter(String character)
	{
		return character.equals(",");
	}
	public boolean isNextSimpleDelimiter()
	{
		return this.isSimpleDelimiter(this.inputStream.peek());
	}
	
	public boolean isObjectAccessor(String character)
	{
		return character.equals(XpellaParserRules.OBJECT_ACCESSOR);
	}
	
	public boolean isStatementOperator(String word)
	{
		return XpellaParserRules.STATEMENT_OPERATORS.contains(word);
	}
	public boolean isExpressionOperator(String word)
	{
		return XpellaParserRules.EXPRESSION_OPERATORS.containsKey(word);
	}
	public boolean isBackOperator(String word)
	{
		return XpellaParserRules.BACK_OPERATORS.contains(word);
	}
	public boolean isFrontOperator(String word)
	{
		return XpellaParserRules.FRONT_OPERATORS.contains(word);
	}
	
	public boolean isOperator(String character)
	{
		return Pattern.matches("[+\\-=/*&|<>:?!^%~]", character);
	}
	
	public boolean isWordBreak(String character)
	{
		return this.isWhitespace(character) ||
				this.isBlockDelimiter(character) ||
				this.isLineDelimiter(character) ||
				this.isObjectAccessor(character) ||
				this.isSimpleDelimiter(character) ||
				this.isOperator(character);
	}
	
	/** Predicate is (next, current) -> shouldContinue */
	public String readWhile(BiFunction<String, String, Boolean> predicate)
	{
		StringBuilder str = new StringBuilder();
		String last = "";
		while(!this.inputStream.isEof() && predicate.apply(this.inputStream.peek(), last))
		{
			last = this.inputStream.next();
			str.append(last);
		}
		return str.toString();
	}
	
	/** Predicate is (next, current) -> shouldContinue */
	public void discardWhile(BiFunction<String, String, Boolean> predicate)
	{
		String last = "";
		while(!this.inputStream.isEof() && predicate.apply(this.inputStream.peek(), last))
			last = this.inputStream.next();
	}
	
	public String readWord()
	{
		return this.readWhile((next, current) -> !this.isWordBreak(next));
	}
	
	public String readBlock(String endDelimiter)
	{
		return this.readWhile((next, current) -> !next.equals(endDelimiter));
	}
	public void discardBlock(String endDelimiter)
	{
		this.readWhile((next, current) -> !next.equals(endDelimiter));
	}
	
	public String readOperator()
	{
		return this.readWhile((next, current) -> this.isOperator(next));
	}
	
	public void eatChar(String character) throws UnexpectedCharacterException
	{
		if(!this.inputStream.peek().equals(character))
			throw new UnexpectedCharacterException(character, this.inputStream.peek(), new XpellaParserBookmark(this.inputStream));
		else
			this.inputStream.next();
	}
	
	public void eatWord(String word) throws UnexpectedCharacterException
	{
		String red = this.readWord();
		if(!red.equals(word))
			throw new UnexpectedCharacterException(word, red, new XpellaParserBookmark(this.inputStream));
	}
	
	public String readSingleLineComment()
	{
		return this.readBlock("\n");
	}
	public String readMultiLineComment()
	{
		return this.readBlock(XpellaParserRules.Comments.MULTILINE_END);
	}
	
	public String readSingleLineDocumentation()
	{
		return this.readBlock("\n");
	}
	public String readMultiLineDocumentation()
	{
		return this.readBlock(XpellaParserRules.Comments.DOC_MULTILINE_END);
	}
	
	public void discardSingleLineComment()
	{
		this.discardBlock("\n");
	}
	public void discardMultiLineComment()
	{
		this.discardBlock(XpellaParserRules.Comments.MULTILINE_END);
	}
	
	public void discardSingleLineDocumentation()
	{
		this.discardBlock("\n");
	}
	public void discardMultiLineDocumentation()
	{
		this.discardBlock(XpellaParserRules.Comments.DOC_MULTILINE_END);
	}
	
	public void skipChar(String character) throws UnexpectedCharacterException
	{
		if(this.inputStream.peek().equals(character))
			this.inputStream.next();
		else
			throw new UnexpectedCharacterException(this.inputStream.peek(), character, new XpellaParserBookmark(this.inputStream));
	}
	
	/** Calls skipWhitespaces(false) */
	public void skipWhitespaces()
	{
		try
		{
			this.skipWhitespaces(false);
		} catch(Exception e)
		{
			// Suppress as it cannot happen
		}
	}
	
	public void skipWhitespaces(boolean atLeastOne) throws UnexpectedCharacterException
	{
		if(atLeastOne && !this.isNextWhitespace())
			throw new UnexpectedCharacterException("whitespace", this.inputStream.peek(), new XpellaParserBookmark(this.inputStream));
		else
		{
			while(!this.inputStream.isEof() && this.isNextWhitespace())
				this.inputStream.next();
		}
	}
}
