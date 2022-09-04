package io.github.agebe.script;

import io.github.agebe.script.run.ScriptRunner;
import io.github.agebe.script.symbol.SymbolTable;

public class Script {

  private SymbolTable symbols;

  Script(SymbolTable symbols) {
    this.symbols = symbols;
  }

  public Object run() {
    new ScriptRunner(symbols).run();
    return null;
  }

  public <T> T runReturning(Class<T> type) {
    return null;
  }

  // TODO also add support for generic types, like e.g. List<String>

  public boolean evalPredicate() {
    run();
    // TODO implement
    return false;
  }

  public double evalDouble() {
    run();
 // TODO implement
    return 0;
  }

  public float evalFloat() {
    run();
 // TODO implement
    return 0;
  }

  public long evalLong() {
    run();
 // TODO implement
    return 0;
  }

  public int evalInt() {
    run();
 // TODO implement
    return 0;
  }

}
