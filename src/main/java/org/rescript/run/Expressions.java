package org.rescript.run;

import org.rescript.symbol.SymbolTable;

public class Expressions {

  private SymbolTable symbols;

  private FunctionCaller functions;

  public Expressions(SymbolTable symbols) {
    super();
    this.symbols = symbols;
    this.functions = new FunctionCaller(symbols, this);
  }

  public SymbolTable getSymbols() {
    return symbols;
  }

  public FunctionCaller getFunctions() {
    return functions;
  }

}
