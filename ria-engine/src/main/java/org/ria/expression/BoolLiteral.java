package org.ria.expression;

import org.ria.run.ScriptContext;
import org.ria.value.BooleanValue;
import org.ria.value.Value;

public class BoolLiteral implements Expression {

  private boolean val;

  public BoolLiteral(String s) {
    val = Boolean.parseBoolean(s);
  }

  public boolean getBool() {
    return val;
  }

  @Override
  public String toString() {
    return "BoolLiteral [val=" + val + "]";
  }

  @Override
  public Value eval(ScriptContext ctx) {
    return BooleanValue.of(val);
  }

  @Override
  public String getText() {
    return Boolean.toString(val);
  }

}
