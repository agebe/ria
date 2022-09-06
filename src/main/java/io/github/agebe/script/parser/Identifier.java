package io.github.agebe.script.parser;

import io.github.agebe.script.run.Expressions;
import io.github.agebe.script.run.Value;

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
