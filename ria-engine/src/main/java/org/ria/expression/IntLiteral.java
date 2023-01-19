package org.ria.expression;

import org.apache.commons.lang3.StringUtils;
import org.ria.run.ScriptContext;
import org.ria.value.IntValue;
import org.ria.value.LongValue;
import org.ria.value.Value;

public class IntLiteral implements Expression {

  private static class P {
    String lit;
    int base;
    public P(String lit, int base) {
      super();
      this.lit = lit;
      this.base = base;
    }
  }

  private String literal;

  private Value value;

  public IntLiteral(String literal) {
    super();
    this.literal = literal;
    this.value = parse(literal);
  }

  private Value parse(String literal) {
    P p = withBase(literal);
    if(literal.endsWith("l") || literal.endsWith("L")) {
      return new LongValue(Long.parseLong(StringUtils.chop(StringUtils.remove(p.lit, '_')), p.base));
    } else {
      return new IntValue(Integer.parseInt(StringUtils.remove(p.lit, '_'), p.base));
    }
  }

  private P withBase(String literal) {
    if(literal.equals("0")) {
      return new P(literal, 10);
    } else if(StringUtils.startsWithIgnoreCase(literal, "0x")) {
      return new P(literal.substring(2), 16);
    } else if(StringUtils.startsWithIgnoreCase(literal, "0b")) {
      return new P(literal.substring(2), 2);
    } else if(StringUtils.startsWithIgnoreCase(literal, "0")) {
      return new P(literal.substring(1), 8);
    } else {
      return new P(literal, 10);
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

  @Override
  public String toString() {
    return getText();
  }

}
