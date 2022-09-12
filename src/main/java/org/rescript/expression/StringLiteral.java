package org.rescript.expression;

import org.rescript.run.ScriptContext;
import org.rescript.value.ObjValue;
import org.rescript.value.Value;

public class StringLiteral implements Expression {

  private String literal;

  public StringLiteral(String literal) {
    super();
    this.literal = literal;
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
    return new ObjValue(String.class, literal);
  }

  @Override
  public String getText() {
    return literal;
  }

}
