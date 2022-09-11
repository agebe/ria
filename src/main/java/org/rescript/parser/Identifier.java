package org.rescript.parser;

import org.rescript.run.Expressions;
import org.rescript.value.Value;

public class Identifier implements ParseItem, TargetExpression {

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
//    return expressions.toValue(expressions.getSymbols().resolve(ident));
    return expressions.getSymbols().resolve(null, ident);
  }

  @Override
  public Value eval(Expressions expressions, Value target) {
    return expressions.getSymbols().resolve(target, ident);
  }

  @Override
  public String getText() {
    return ident;
  }

}
