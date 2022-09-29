package org.rescript;

import org.rescript.parser.Parser;
import org.rescript.run.ScriptRunner;
import org.rescript.symbol.SymbolTable;
import org.rescript.value.Value;

public class Script {

  private SymbolTable symbols;

  public Script() {
    symbols = new SymbolTable();
  }

  public Script(SymbolTable symbols) {
    this.symbols = symbols;
  }

  public Value run() {
    try {
      return new ScriptRunner(symbols).run();
    } catch(Exception e) {
      throw new ScriptException("script execution failed", e);
    }
  }

  public Value run(String script) {
    return parse(script).run();
  }

  public <T> T runReturning(Class<T> type) {
    Value val = run();
    return val.isNull()?null:type.cast(val.val());
  }

  public <T> T runReturning(String script, Class<T> type) {
    return parse(script).runReturning(type);
  }

  // TODO also add support for generic types, like e.g. List<String>

  public boolean evalPredicate() {
    return run().toBoolean();
  }

  public boolean evalPredicate(String script) {
    return parse(script).evalPredicate();
  }

  public double evalDouble() {
    return run().toDouble();
  }

  public double evalDouble(String script) {
    return parse(script).evalDouble();
  }

  public float evalFloat() {
    return run().toFloat();
  }

  public float evalFloat(String script) {
    return parse(script).evalFloat();
  }

  public long evalLong() {
    return run().toLong();
  }

  public long evalLong(String script) {
    return parse(script).evalLong();
  }

  public int evalInt() {
    return run().toInt();
  }

  public int evalInt(String script) {
    return parse(script).evalInt();
  }

  public void setVariable(String name, Object val) {
    if(val != null) {
      symbols.assignVar(name, Value.of(val.getClass(), val));
    } else {
      // FIXME support null values
      throw new ScriptException("null values not supported");
    }
  }

  public Object getVariable(String name) {
    Value val = symbols.getVariable(name);
    return val!=null?val.val():null;
  }

  private Script parse(String script) {
    symbols = this.symbols.merge(new Parser().parse(script));
    return this;
  }

}
