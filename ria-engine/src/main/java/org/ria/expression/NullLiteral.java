package org.ria.expression;

import org.ria.run.ScriptContext;
import org.ria.value.ObjValue;
import org.ria.value.Value;

public class NullLiteral implements Expression {

  @Override
  public Value eval(ScriptContext ctx) {
    return ObjValue.NULL;
  }

  @Override
  public String getText() {
    return "null";
  }

}
