package org.rescript.expression;

import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

public class NewArrayOp implements Expression {

  private String type;

  private Expression size;

  public NewArrayOp(String type, Expression size) {
    super();
    this.type = type;
    this.size = size;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    return ArrayUtil.newArray(ctx.getSymbols().getJavaSymbols().resolveType(type), size.eval(ctx).toInt());
  }

}
