package org.rescript.expression;

import org.rescript.ScriptException;
import org.rescript.run.ScriptContext;
import org.rescript.value.Array;
import org.rescript.value.Value;

public class ArrayAccessOp implements Expression {

  private Expression arrayExpr;

  private Expression indexExpr;

  public ArrayAccessOp(Expression arrayExpr, Expression indexExpr) {
    super();
    this.arrayExpr = arrayExpr;
    this.indexExpr = indexExpr;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value array = arrayExpr.eval(ctx);
    if(array instanceof Array arr) {
      Value index = indexExpr.eval(ctx);
      return arr.get(index.toInt());
    } else {
      throw new ScriptException("array expected from expression but got '%s'".formatted(array.type()));
    }
  }

}
