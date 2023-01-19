package org.ria.expression;

import org.ria.run.ScriptContext;
import org.ria.symbol.SymbolNotFoundException;
import org.ria.value.ObjValue;
import org.ria.value.Value;

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
