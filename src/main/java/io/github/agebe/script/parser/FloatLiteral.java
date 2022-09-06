package io.github.agebe.script.parser;

import io.github.agebe.script.run.DoubleValue;
import io.github.agebe.script.run.Expressions;
import io.github.agebe.script.run.FloatValue;
import io.github.agebe.script.run.Value;

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
