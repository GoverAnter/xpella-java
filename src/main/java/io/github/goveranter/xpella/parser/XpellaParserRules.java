package io.github.goveranter.xpella.parser;

import java.util.List;
import java.util.Map;

public class XpellaParserRules
{
	private XpellaParserRules() { /* This class should only be used statically */ }
	
	public static final String LINE_DELIMITER = ";";
	public static final String OBJECT_ACCESSOR = ".";
	// The char to escape a string delimiter
	public static final String STRING_ESCAPE = "\\";
	
	public static final String THIS_KEYWORD = "this";
	public static final String TYPE_DECLARATION_KEYWORD = "class";
	public static final String TYPE_INSTANTIATION_KEYWORD = "new";
	public static final String OPERATOR_METHOD_PREFIX = "operator";
	public static final String DEFAULT_VISIBILITY = "protected";
	
	public static final List<String> CONSTRUCTS = List.of(
			"if",
			"else",
			"for",
			"do",
			"while",
			"switch",
			"case",
			"default",
			"try",
			"catch",
			"finally"
	);
	
	public static final List<String> MODIFIERS = List.of(
			"static",
			"const"
	);
	
	public static final List<String> VISIBILITIES = List.of(
			"public",
			"protected",
			"private"
	);
	
	public static final List<String> EXPRESSION_KEYWORDS = List.of(
			"instanceof", // True if left is of type right
			// [expression] instanceof [type]
			"typeof" // Gives the type object
			// typeof [expression]
	);
	
	public static final List<String> STATEMENT_KEYWORDS = List.of(
			"return", // Indicates the function output, breaks function flow
			// return [expression]?;
			"break", // Get out of a loop
			// break;
			"continue" // Do next loop iteration
			// continue;
	);
	
	public static final List<String> TYPES = List.of(
			"any", // Any is special because it is only a syntaxic construction
			"function",
			"type",
			"object",
			"int",
			"string",
			"float",
			"boolean",
			"bool" // Same as boolean, only a syntaxic construction
	);
	
	// A string termination delimiter MUST match its start delimiter
	public static final List<String> STRING_DELIMITERS = List.of(
			"'",
			"\"",
			"`"
	);
	
	// OPERATORS
	
	// Statement operators always have the lowest precedence
	public static final List<String> STATEMENT_OPERATORS = List.of(
			"=",
			"+=",
			"-=",
			"*=",
			"/=",
			"%="
	);
	
	// Back operators are always prioritized
	public static final List<String> BACK_OPERATORS = List.of(
			"++", // Can also be front operators with the same priority
			"--",
			"!", // Boolean not
			"~" // Binary not
	);

	// Front operators are always prioritized
	public static final List<String> FRONT_OPERATORS = List.of(
			"++", // Can also be back operators with the same priority
			"--"
	);
	
	public static final Map<String, Integer> EXPRESSION_OPERATORS = Map.ofEntries(
			Map.entry("==", 10), // Equality
			Map.entry("!=", 10), // Inequality
			Map.entry(">=", 15), // More than or equal
			Map.entry("<=", 15), // Less than or equal
			Map.entry("<", 15), // Less than
			Map.entry(">", 15), // More than
			Map.entry("?", 2), // Ternary condition
			Map.entry(":", 2), // Ternary condition execution
			Map.entry("&&", 4), // Boolean and
			Map.entry("||", 3), // Boolean or
			Map.entry(">>", 17), // Binary right shift
			Map.entry("<<", 17), // Binary left shift
			Map.entry("&", 8), // Binary and
			Map.entry("|", 6), // // Binary or
			Map.entry("^", 7), // Binary xor
			Map.entry("+", 20), // Plus
			Map.entry("-", 20), // Minus
			Map.entry("*", 30), // Times
			Map.entry("/", 30), // Divided by
			Map.entry("%", 30) // Remaining
	);
	
	public static final List<String> EXPRESSION_OPERATORS_ARRAY = List.of(
			"==", // Equality
			"!=", // Inequality
			">=", // More than or equal
			"<=", // Less than or equal
			"<", // Less than
			">", // More than
			"?", // Ternary condition
			":", // Ternary condition execution
			"&&", // Boolean and
			"||", // Boolean or
			">>", // Binary right shift
			"<<", // Binary left shift
			"&", // Binary and
			"|", // Binary or
			"^", // Binary xor
			"+", // Plus
			"-", // Minus
			"*", // Times
			"/", // Divided by
			"%" // Remaining
	);
	
	// Comments
	// Only a helper object to expose a clean interface
	public static final class Comments
	{
		private Comments() { /* This class should only be used statically */ }
		
		public static final String SINGLE_LINE = "//";
		public static final String MULTILINE_START = "/*";
		public static final String MULTILINE_END = "*/";
		public static final String DOC_SINGLE_LINE = "///";
		public static final String DOC_MULTILINE_START = "/**";
		public static final String DOC_MULTILINE_END = "*/";
	}
}
