package org.rescript.expression;

import org.rescript.run.ScriptContext;
import org.rescript.value.Value;
import org.rescript.value.VoidValue;

public class VoidLiteral implements Expression {

  @Override
  public Value eval(ScriptContext ctx) {
    return VoidValue.VOID;
  }

}
