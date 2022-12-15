package org.rescript.expression;

import org.rescript.run.ScriptContext;
import org.rescript.symbol.SymbolNotFoundException;
import org.rescript.value.ObjValue;
import org.rescript.value.Value;

public class TypeOfOp implements Expression {

  private Expression expr;

  public TypeOfOp(Expression expr) {
    super();
    this.expr = expr;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    try {
      Value val = expr.eval(ctx);
      return new ObjValue(String.class, val!=null?val.typeOf():"undefined");
    } catch(SymbolNotFoundException e) {
      return new ObjValue(String.class, "undefined");
    }
  }

}
