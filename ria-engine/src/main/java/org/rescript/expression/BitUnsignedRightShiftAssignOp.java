package org.rescript.expression;

import org.rescript.value.Value;

public class BitUnsignedRightShiftAssignOp extends XAssignOp {

  public BitUnsignedRightShiftAssignOp(Identifier identifier, Expression expression) {
    super(identifier, expression);
  }

  @Override
  protected Value doOp(Value v1, Value v2) {
    return BitUnsignedRightShiftOp.unsignedRightShift(v1, v2, "<<<=");
  }

}
