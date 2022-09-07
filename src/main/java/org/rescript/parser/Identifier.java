package org.rescript.parser;

import org.rescript.run.Expressions;
import org.rescript.run.Value;

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

  @Override
  public Value eval(Expressions expressions) {
    return expressions.toValue(expressions.getSymbols().resolve(ident));
  }

}
