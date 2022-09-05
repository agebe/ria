package io.github.agebe.script.parser;

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

}
