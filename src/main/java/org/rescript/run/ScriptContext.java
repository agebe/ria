package org.rescript.run;

import org.rescript.statement.Statement;
import org.rescript.symbol.SymbolTable;
import org.rescript.value.Value;
import org.rescript.value.VoidValue;

public class ScriptContext {

  private SymbolTable symbols;

  private FunctionCaller functions;

  private Statement current;

  private Value lastResult = new VoidValue();

  private boolean returnFlag;

  public ScriptContext(SymbolTable symbols) {
    super();
    this.symbols = symbols;
    this.functions = new FunctionCaller(this);
    this.current = symbols.getScriptSymbols().getEntryPoint();
  }

  public Statement getCurrent() {
    return current;
  }

  public void setCurrent(Statement current) {
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

  public FunctionCaller getFunctions() {
    return functions;
  }

  public boolean isReturnFlag() {
    return returnFlag;
  }

  public void setReturnFlag(boolean returnFlag) {
    this.returnFlag = returnFlag;
  }

}
