package org.ria.expression;

import org.ria.value.Value;

public class DivAssignOp extends XAssignOp {

  public DivAssignOp(Identifier identifier, Expression expression) {
    super(identifier, expression);
  }

  @Override
  protected Value doOp(Value v1, Value v2) {
    return DivOp.div(v1, v2, "/=");
  }

}
