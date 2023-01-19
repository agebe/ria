package org.rescript.expression;

import org.rescript.value.Value;

public class ModAssignOp extends XAssignOp {

  public ModAssignOp(Identifier identifier, Expression expression) {
    super(identifier, expression);
  }

  @Override
  protected Value doOp(Value v1, Value v2) {
    return ModOp.mod(v1, v2, "%=");
  }

}
