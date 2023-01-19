package org.rescript.expression;

import org.rescript.value.Value;

public class BitRightShiftAssignOp extends XAssignOp {

  public BitRightShiftAssignOp(Identifier identifier, Expression expression) {
    super(identifier, expression);
  }

  @Override
  protected Value doOp(Value v1, Value v2) {
    return BitRightShiftOp.rightShift(v1, v2, ">>=");
  }

}
