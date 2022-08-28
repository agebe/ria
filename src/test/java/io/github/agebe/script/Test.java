package io.github.agebe.script;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import io.github.agebe.script.antlr.ScriptLexer;
import io.github.agebe.script.antlr.ScriptParser;

public class Test {

  private static final String SCRIPT = """
      var v1;
      var v2 = foo(myVar, "myStringLiteral");
      var v3 = "12345";
      v1 = v2;
      // dot operator not supported yet
      return v1.equals("12345");
      """;

  public static void main(String[] args) {
    ScriptLexer lexer = new ScriptLexer(CharStreams.fromString(SCRIPT));
    // antlr uses the ConsoleErrorListener by default
    // showing here how to remove it for later
    lexer.removeErrorListeners();
    // add it back in for now ...
    lexer.addErrorListener(ConsoleErrorListener.INSTANCE);
//    lexer.getAllTokens().forEach(System.out::println);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    ScriptParser parser = new ScriptParser(tokens);
    // same is with the parser error listener ...
    //parser.getErrorListeners().forEach(System.out::println);
    SyntaxExceptionErrorListener errorListener = new SyntaxExceptionErrorListener();
    parser.addErrorListener(errorListener);
    ScriptParser.ScriptContext tree = parser.script();
//    ParseTreeWalker.DEFAULT.walk(new MyParseTreeListener(), tree);
    ParseTreeWalker.DEFAULT.walk(new MyScriptListener(), tree);
  }

}
