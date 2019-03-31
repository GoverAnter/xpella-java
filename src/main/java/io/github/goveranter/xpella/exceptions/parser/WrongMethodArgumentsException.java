package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.headers.XpellaParserArgumentHeader;
import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

import java.util.List;
import java.util.stream.Collectors;

public class WrongMethodArgumentsException extends ParserException
{
	public WrongMethodArgumentsException(List<XpellaParserArgumentHeader> expected, List<String> actual, XpellaParserBookmark bookmark)
	{
		super("Expected arguments " + expected.stream().map(XpellaParserArgumentHeader::getType).collect(Collectors.joining(", ", "[", "]")) +
				", got " + actual.stream().collect(Collectors.joining(", ", "[", "]")), bookmark);
	}
}
