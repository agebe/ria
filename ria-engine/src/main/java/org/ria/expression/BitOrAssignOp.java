package org.ria.expression;

import org.ria.value.Value;

public class BitOrAssignOp extends XAssignOp {

  public BitOrAssignOp(Identifier identifier, Expression expression) {
    super(identifier, expression);
  }

  @Override
  protected Value doOp(Value v1, Value v2) {
    return BitOrOp.or(v1, v2, "|=");
  }

}
