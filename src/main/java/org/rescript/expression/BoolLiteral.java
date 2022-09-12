package org.rescript.expression;

import org.rescript.parser.ParseItem;
import org.rescript.run.Expressions;
import org.rescript.value.BooleanValue;
import org.rescript.value.Value;

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

  @Override
  public String getText() {
    return Boolean.toString(val);
  }

}
