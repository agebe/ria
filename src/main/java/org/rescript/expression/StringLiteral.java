package org.rescript.expression;

import org.apache.commons.text.StringEscapeUtils;
import org.rescript.run.ScriptContext;
import org.rescript.value.ObjValue;
import org.rescript.value.Value;

public class StringLiteral implements Expression {

  private String literal;

  private String unescaped;

  private Value val;

  public StringLiteral(String literal) {
    super();
    this.literal = literal;
    this.unescaped = StringEscapeUtils.unescapeJava(literal).intern();
    val = new ObjValue(String.class, unescaped);
  }

  public String getLiteral() {
    return literal;
  }

  public String getUnescaped() {
    return unescaped;
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
