package org.rescript.parser;

import org.rescript.run.DoubleValue;
import org.rescript.run.Expressions;
import org.rescript.run.FloatValue;
import org.rescript.run.Value;

public class FloatLiteral implements ParseItem, Expression {

  private Value value;

  public FloatLiteral(String literal) {
    super();
    this.value = parse(literal);
  }

  private Value parse(String literal) {
    if(literal.endsWith("f") || literal.endsWith("F")) {
      return new FloatValue(Float.parseFloat(literal));
    } else {
      return new DoubleValue(Double.parseDouble(literal));
    }
  }

  @Override
  public Value eval(Expressions expressions) {
    return value;
  }

}
