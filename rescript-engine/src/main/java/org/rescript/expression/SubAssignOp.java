package org.rescript.expression;

import org.rescript.value.Value;

public class SubAssignOp extends XAssignOp {

  public SubAssignOp(Identifier identifier, Expression expression) {
    super(identifier, expression);
  }

  @Override
  protected Value doOp(Value v1, Value v2) {
    return SubOp.sub(v1, v2, "-=");
  }

}
