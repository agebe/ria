package org.rescript.parser;

import org.rescript.run.BooleanValue;
import org.rescript.run.Expressions;
import org.rescript.run.Value;

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
