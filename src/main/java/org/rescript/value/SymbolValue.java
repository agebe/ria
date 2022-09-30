package org.rescript.value;

import org.rescript.symbol.Symbol;

public class SymbolValue extends EvaluatedFromValue {

  private Symbol symbol;

  public SymbolValue(Symbol symbol) {
    super();
    this.symbol = symbol;
  }

  protected Value getWrapped() {
    return symbol.get();
  }

  public Symbol getSymbol() {
    return symbol;
  }

}
