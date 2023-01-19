package org.ria.expression;

import org.apache.commons.text.StringEscapeUtils;
import org.ria.ScriptException;
import org.ria.run.ScriptContext;
import org.ria.value.CharValue;
import org.ria.value.Value;

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
