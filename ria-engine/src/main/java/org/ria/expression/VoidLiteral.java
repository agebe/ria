package org.ria.expression;

import org.ria.run.ScriptContext;
import org.ria.value.Value;
import org.ria.value.VoidValue;

public class VoidLiteral implements Expression {

  @Override
  public Value eval(ScriptContext ctx) {
    return VoidValue.VOID;
  }

}
