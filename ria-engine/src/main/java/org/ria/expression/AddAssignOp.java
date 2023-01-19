package org.ria.expression;

import org.ria.value.Value;

public class AddAssignOp extends XAssignOp {

  public AddAssignOp(Identifier identifier, Expression expression) {
    super(identifier, expression);
  }

  @Override
  protected Value doOp(Value v1, Value v2) {
    return AddOp.add(v1, v2, "+=");
  }

}
