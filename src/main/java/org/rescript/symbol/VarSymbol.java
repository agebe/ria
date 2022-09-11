package org.rescript.symbol;

import org.rescript.value.Value;

public class VarSymbol implements Symbol {

  private String name;

  private Value val;

  public VarSymbol(String name, Value val) {
    super();
    this.name = name;
    this.val = val;
  }

  public String getName() {
    return name;
  }

  public Value getVal() {
    return val;
  }

}
