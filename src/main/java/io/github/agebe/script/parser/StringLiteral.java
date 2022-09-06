package io.github.agebe.script.parser;

import io.github.agebe.script.run.Expressions;
import io.github.agebe.script.run.ObjValue;
import io.github.agebe.script.run.Value;

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
