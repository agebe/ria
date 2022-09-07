package org.rescript;

import org.rescript.run.ScriptRunner;
import org.rescript.run.Value;
import org.rescript.symbol.SymbolTable;

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
    return run().toLong();
  }

  public int evalInt() {
    return run().toInt();
  }

}
