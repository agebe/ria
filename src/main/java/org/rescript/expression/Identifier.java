package org.rescript.expression;

import java.util.List;

import org.rescript.run.ScriptContext;
import org.rescript.symbol.SymbolNotFoundException;
import org.rescript.value.UnresolvedIdentifier;
import org.rescript.value.Value;

public class Identifier implements TargetExpression, Ident {

  private String ident;

  public Identifier(String ident) {
    super();
    this.ident = ident;
  }

  @Override
  public String getIdent() {
    return ident;
  }

  @Override
  public String toString() {
    return "Identifier [ident=" + ident + "]";
  }

  @Override
  public Value eval(ScriptContext ctx) {
    try {
      return ctx.getSymbols().resolveVarOrTypeOrStaticMember(ident);
    } catch(SymbolNotFoundException e) {
      return new UnresolvedIdentifier(ident);
    }
  }

  @Override
  public Value eval(ScriptContext ctx, Value target) {
    if(target instanceof UnresolvedIdentifier uident) {
      String i = uident.getIdentifier() + "." + ident;
      try {
        return ctx.getSymbols().resolveVarOrTypeOrStaticMember(i);
      } catch(SymbolNotFoundException e) {
        return new UnresolvedIdentifier(i);
      }
    } else {
      return ctx.getSymbols().getJavaSymbols().resolveRemaining(List.of(ident), target);
    }
  }

  @Override
  public String getText() {
    return ident;
  }

}
