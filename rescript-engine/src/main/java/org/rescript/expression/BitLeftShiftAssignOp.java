package org.rescript.expression;

import org.rescript.value.Value;

public class BitLeftShiftAssignOp extends XAssignOp {

  public BitLeftShiftAssignOp(Identifier identifier, Expression expression) {
    super(identifier, expression);
  }

  @Override
  protected Value doOp(Value v1, Value v2) {
    return BitLeftShiftOp.leftShift(v1, v2, "<<=");
  }

}
