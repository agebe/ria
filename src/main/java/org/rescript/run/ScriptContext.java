package org.rescript.run;

import org.rescript.parser.AstNode;
import org.rescript.symbol.SymbolTable;
import org.rescript.value.Value;
import org.rescript.value.VoidValue;

public class ScriptContext {

  private SymbolTable symbols;

  private AstNode current;

  private Value lastResult = new VoidValue();

  public ScriptContext(SymbolTable symbols) {
    super();
    this.symbols = symbols;
  }

  public AstNode getCurrent() {
    return current;
  }

  public void setCurrent(AstNode current) {
    this.current = current;
  }

  public Value getLastResult() {
    return lastResult;
  }

  public void setLastResult(Value lastResult) {
    this.lastResult = lastResult;
  }

  public SymbolTable getSymbols() {
    return symbols;
  }

}
