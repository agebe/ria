package org.rescript.value;

import org.rescript.symbol.VarSymbol;

public class VariableValue extends EvaluatedFromValue {

  private VarSymbol var;

  public VariableValue(VarSymbol var) {
    super();
    this.var = var;
  }

  protected Value getWrapped() {
    return var.getVal();
  }

}
