package org.ria.parser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.lang3.StringUtils;
import org.ria.ScriptException;
import org.ria.antlr.ScriptLexer;
import org.ria.antlr.ScriptParser;
import org.ria.statement.HeaderEnterStatement;
import org.ria.statement.HeaderExitStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Parser {

  private static final Logger log = LoggerFactory.getLogger(Parser.class);

  private boolean showErrorsOnConsole;

  public Parser(boolean showErrorsOnConsole) {
    super();
    this.showErrorsOnConsole = showErrorsOnConsole;
  }

  public ParserListener parse(
      String script,
      HeaderEnterStatement headerEnter,
      HeaderExitStatement headerExit) {
    log.debug("parsing script '{}'", script);
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
    ParserListener listener = new ParserListener(headerEnter, headerExit);
    ParseTreeWalker.DEFAULT.walk(listener, scriptCtx);
    return listener;
  }

}
