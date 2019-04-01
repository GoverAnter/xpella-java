package io.github.goveranter.xpella;

import io.github.goveranter.xpella.ast.XpellaASTProgram;
import io.github.goveranter.xpella.exceptions.parser.ParserException;
import io.github.goveranter.xpella.exceptions.scopes.ScopeException;
import io.github.goveranter.xpella.parser.implementation.XpellaParser;

public class TestApplication
{
	public static void main(String[] args)
	{
		String code = "class OtherTest {\n  public static int q = 2;\n  public int getInt(int w = 2) {\n    return 10 + w;\n  }\n}\n\nclass Test {\n  public int i = 0;\n  public int testSomething(int z) {\n    z = this.i + 1 + OtherTest.q;\n    this.i += z * 2;\n    OtherTest oTest = new OtherTest();\n    return z * oTest.getInt();\n  }\n  public static void main() {\n    Test test = new Test();\n    int result = test.testSomething(20);\n    if (result == 72 && test.i == 6) {\n      return;\n    }\n    else {\n      return;\n    }\n  }\n}";
		XpellaParser parser = new XpellaParser(code);
		
		try
		{
			XpellaASTProgram program = parser.executeParse();
		} catch(ParserException | ScopeException e)
		{
			e.printStackTrace();
		}
	}
}
