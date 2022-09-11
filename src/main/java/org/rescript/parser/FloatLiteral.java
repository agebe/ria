package org.rescript.parser;

import org.apache.commons.lang3.StringUtils;
import org.rescript.run.Expressions;
import org.rescript.value.DoubleValue;
import org.rescript.value.FloatValue;
import org.rescript.value.Value;

public class FloatLiteral implements ParseItem, Expression {

  private Value value;

  private String literal;

  public FloatLiteral(String literal) {
    super();
    this.literal = literal;
    this.value = parse(literal);
  }

  private Value parse(String literal) {
    if(literal.endsWith("f") || literal.endsWith("F")) {
      return new FloatValue(Float.parseFloat(StringUtils.remove(literal, '_')));
    } else {
      return new DoubleValue(Double.parseDouble(StringUtils.remove(literal, '_')));
    }
  }

  @Override
  public Value eval(Expressions expressions) {
    return value;
  }

  @Override
  public String getText() {
    return literal;
  }

}
