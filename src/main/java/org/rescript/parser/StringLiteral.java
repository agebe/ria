package org.rescript.parser;

import org.rescript.run.Expressions;
import org.rescript.run.ObjValue;
import org.rescript.run.Value;

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

}