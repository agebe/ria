package org.rescript.parser;

import org.rescript.run.Expressions;
import org.rescript.value.ObjValue;
import org.rescript.value.Value;

public class StringLiteral implements ParseItem, Expression {

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
  public Value eval(Expressions expressions) {
    return new ObjValue(String.class, literal);
  }

  @Override
  public String getText() {
    return literal;
  }

}
