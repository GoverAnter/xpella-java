package io.github.goveranter.xpella.exceptions.parser;

import io.github.goveranter.xpella.parser.helpers.XpellaParserBookmark;

import java.util.List;
import java.util.stream.Collectors;

public class MethodNotFoundException extends ParserException
{
	public MethodNotFoundException(String identifier, List<String> args, String type, XpellaParserBookmark bookmark)
	{
		super("Cannot find method \"" + identifier + args.stream().collect(Collectors.joining(", ", "(", ")")) + "\" in type \"" + type + "\"", bookmark);
	}
}
