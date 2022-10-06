package org.rescript.expression;

import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

public class DottedIdentifier implements Expression {

  private String identifier;

  public DottedIdentifier(String identifier) {
    super();
    this.identifier = identifier;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    return ctx.getSymbols().resolveVarOrTypeOrStaticMember(identifier);
  }

  @Override
  public String getText() {
    return identifier;
  }

  @Override
  public String toString() {
    return "DottedIdentifier [identifier=" + identifier + "]";
  }

}
