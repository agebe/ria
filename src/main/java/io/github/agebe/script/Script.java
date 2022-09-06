package io.github.agebe.script;

import io.github.agebe.script.run.ScriptRunner;
import io.github.agebe.script.run.Value;
import io.github.agebe.script.symbol.SymbolTable;

public class Script {

  private SymbolTable symbols;

  Script(SymbolTable symbols) {
    this.symbols = symbols;
  }

  public Value run() {
    return new ScriptRunner(symbols).run();
  }

  public <T> T runReturning(Class<T> type) {
    run();
    return null;
  }

  // TODO also add support for generic types, like e.g. List<String>

  public boolean evalPredicate() {
    return run().toBoolean();
  }

  public double evalDouble() {
    return run().toDouble();
  }

  public float evalFloat() {
    return run().toFloat();
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
