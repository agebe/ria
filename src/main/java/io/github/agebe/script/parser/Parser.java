package io.github.agebe.script.parser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.lang3.StringUtils;

import io.github.agebe.script.ScriptException;
import io.github.agebe.script.antlr.ScriptLexer;
import io.github.agebe.script.antlr.ScriptParser;
import io.github.agebe.script.symbol.SymbolTable;

public class Parser {

  private boolean showErrorsOnConsole;

  public Parser(boolean showErrorsOnConsole) {
    super();
    this.showErrorsOnConsole = showErrorsOnConsole;
  }

  public SymbolTable parse(String script) {
    if(StringUtils.isBlank(script)) {
      throw new ScriptException("no script has been setup");
    }
    ScriptLexer lexer = new ScriptLexer(CharStreams.fromString(script));
    // antlr uses the ConsoleErrorListener by default
    lexer.removeErrorListeners();
    if(showErrorsOnConsole) {
      lexer.addErrorListener(ConsoleErrorListener.INSTANCE);
    }
    lexer.addErrorListener(new SyntaxExceptionErrorListener());
    //  lexer.getAllTokens().forEach(System.out::println);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    ScriptParser parser = new ScriptParser(tokens);
    parser.removeErrorListeners();
    if(showErrorsOnConsole) {
      parser.addErrorListener(ConsoleErrorListener.INSTANCE);
    }
    parser.addErrorListener(new SyntaxExceptionErrorListener());
    ScriptParser.ScriptContext scriptCtx = parser.script();
    ParserListener listener = new ParserListener();
    ParseTreeWalker.DEFAULT.walk(listener, scriptCtx);
    return listener.getSymbols();
  }

}
