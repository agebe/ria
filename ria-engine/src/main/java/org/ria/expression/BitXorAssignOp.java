package org.ria.expression;

import org.ria.value.Value;

public class BitXorAssignOp extends XAssignOp {

  public BitXorAssignOp(Identifier identifier, Expression expression) {
    super(identifier, expression);
  }

  @Override
  protected Value doOp(Value v1, Value v2) {
    return BitXorOp.xor(v1, v2, "^=");
  }

}
