package io.github.agebe.script.parser;

import io.github.agebe.script.run.BooleanValue;
import io.github.agebe.script.run.Expressions;
import io.github.agebe.script.run.Value;

public class BoolLiteral implements ParseItem, Expression {

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
  public Value eval(Expressions expressions) {
    return new BooleanValue(val);
  }

}
