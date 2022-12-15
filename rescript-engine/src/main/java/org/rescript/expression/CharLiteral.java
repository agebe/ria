package org.rescript.expression;

import org.apache.commons.text.StringEscapeUtils;
import org.rescript.ScriptException;
import org.rescript.run.ScriptContext;
import org.rescript.value.CharValue;
import org.rescript.value.Value;

public class CharLiteral implements Expression {

  private String literal;

  private Value val;

  public CharLiteral(String literal) {
    super();
    this.literal = literal;
    String s = StringEscapeUtils.unescapeJava(literal).intern();
    if(s.length() == 1) {
      val = new CharValue(s.charAt(0));
    } else {
      throw new ScriptException("char literal '%s' requires single character but has '%s' "
          .formatted(literal, literal.length()));
    }
  }

  @Override
  public Value eval(ScriptContext ctx) {
    return val;
  }

  @Override
  public String toString() {
    return "CharLiteral [literal=" + literal + ", val=" + val + "]";
  }

}
