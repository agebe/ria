package org.rescript.expression;

import org.rescript.value.Value;

public class BitAndAssignOp extends XAssignOp {

  public BitAndAssignOp(Identifier identifier, Expression expression) {
    super(identifier, expression);
  }

  @Override
  protected Value doOp(Value v1, Value v2) {
    return BitAndOp.and(v1, v2, "&=");
  }

}
