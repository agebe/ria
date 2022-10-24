package org.rescript.expression;

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
    if(literal.length() == 1) {
      val = new CharValue(literal.charAt(0));
    } else {
      throw new ScriptException("char literal not supported " + literal);
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
