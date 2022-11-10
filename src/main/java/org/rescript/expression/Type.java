package org.rescript.expression;

import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

public class Type implements Expression, Ident {

  private String identifier;

  public Type(String identifier) {
    super();
    this.identifier = identifier;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    return ctx.getSymbols().resolveVarOrTypeOrStaticMember(identifier);
  }

  @Override
  public String getIdent() {
    return identifier;
  }

  @Override
  public String getText() {
    return getIdent();
  }

  @Override
  public String toString() {
    return "Type [identifier=" + identifier + "]";
  }

}
