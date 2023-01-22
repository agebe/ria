package org.ria.expression;

import java.util.List;

import org.ria.run.ScriptContext;
import org.ria.symbol.SymbolNotFoundException;
import org.ria.value.UnresolvedIdentifier;
import org.ria.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Identifier implements TargetExpression, Ident {

  private static final Logger log = LoggerFactory.getLogger(Identifier.class);

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
      Value v = ctx.getSymbols().resolveVarOrTypeOrStaticMember(ident, ctx);
      log.debug("ident resolved to '{}'", v);
      return v;
    } catch(SymbolNotFoundException e) {
      return new UnresolvedIdentifier(ident);
    }
  }

  @Override
  public Value eval(ScriptContext ctx, Value target) {
    if(target instanceof UnresolvedIdentifier uident) {
      String i = uident.getIdentifier() + "." + ident;
      try {
        return ctx.getSymbols().resolveVarOrTypeOrStaticMember(i, ctx);
      } catch(SymbolNotFoundException e) {
        return new UnresolvedIdentifier(i);
      }
    } else {
      return ctx.getSymbols().getJavaSymbols().resolveRemaining(List.of(ident), target, ctx);
    }
  }

  @Override
  public String getText() {
    return ident;
  }

}
