package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

public class ComplexAssignmentException extends ParserException
{
	public ComplexAssignmentException(XpellaParserBookmark bookmark)
	{
		super("Cannot use a complex assigment on a variable declaration, only simple assignment operator \"=\" is accepted", bookmark);
	}
}
