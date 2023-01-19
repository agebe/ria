package org.ria.expression;

import org.apache.commons.lang3.StringUtils;
import org.ria.run.ScriptContext;
import org.ria.value.DoubleValue;
import org.ria.value.FloatValue;
import org.ria.value.Value;

public class FloatLiteral implements Expression {

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
  public Value eval(ScriptContext ctx) {
    return value;
  }

  @Override
  public String getText() {
    return literal;
  }

}
