package org.rescript.expression;

import org.apache.commons.text.StringEscapeUtils;
import org.rescript.run.ScriptContext;
import org.rescript.value.ObjValue;
import org.rescript.value.Value;

public class StringLiteral implements Expression {

  private String literal;

  private Value val;

  public StringLiteral(String literal) {
    super();
    this.literal = literal;
    val = new ObjValue(String.class, StringEscapeUtils.unescapeJava(literal).intern());
  }

  public String getLiteral() {
    return literal;
  }

  @Override
  public String toString() {
    return "StringLiteral [literal=" + literal + "]";
  }

  @Override
  public Value eval(ScriptContext ctx) {
    return val;
  }

  @Override
  public String getText() {
    return literal;
  }

}
