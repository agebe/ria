package io.github.agebe.script.run;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.agebe.script.parser.AstNode;
import io.github.agebe.script.symbol.SymbolTable;

public class ScriptRunner {

  private static final Logger log = LoggerFactory.getLogger(ScriptRunner.class);

  private SymbolTable symbols;

  private ExpressionResult lastResult;

  public ScriptRunner(SymbolTable symbols) {
    super();
    this.symbols = symbols;
  }

  public ExpressionResult run() {
    AstNode current = symbols.getEntryPoint();
    while(current != null) {
      log.debug("exec next statement '{}'", current);
      // TODO run statement
      current = current.next(true);
    }
    log.debug("exit script run");
    return lastResult;
  }

}
