package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

import java.util.List;
import java.util.stream.Collectors;

public class NoMethodReturnTypeException extends ParserException
{
	public NoMethodReturnTypeException(String identifier, List<String> args, XpellaParserBookmark bookmark)
	{
		super("No return type found for method call \"" + identifier + args.stream().collect(Collectors.joining(", ", "(", ")")) + "\"", bookmark);
	}
}
