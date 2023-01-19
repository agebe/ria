package org.ria.expression;

import org.ria.value.Value;

public class MulAssignOp extends XAssignOp {

  public MulAssignOp(Identifier identifier, Expression expression) {
    super(identifier, expression);
  }

  @Override
  protected Value doOp(Value v1, Value v2) {
    return MulOp.mul(v1, v2, "*=");
  }

}
