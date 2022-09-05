package io.github.agebe.script.parser;

public class Identifier implements ParseItem, Expression {

  private String ident;

  public Identifier(String ident) {
    super();
    this.ident = ident;
  }

  public String getIdent() {
    return ident;
  }

  @Override
  public String toString() {
    return "Identifier [ident=" + ident + "]";
  }

}
