package org.rescript.expression;

import org.rescript.run.ScriptContext;
import org.rescript.value.ObjValue;
import org.rescript.value.Value;

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
