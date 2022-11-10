package org.rescript.expression;

import org.rescript.run.ScriptContext;
import org.rescript.value.ConstructorValue;
import org.rescript.value.Value;

public class ConstructorReference implements TargetExpression {

  private String type;

  public ConstructorReference(String type) {
    super();
    this.type = type;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Class<?> cls = ctx.getSymbols().getJavaSymbols().resolveType(type);
    return new ConstructorValue(cls);
  }

  @Override
  public Value eval(ScriptContext ctx, Value target) {
    Class<?> cls = ctx.getSymbols().getJavaSymbols().resolveType(target.type().getName()+"."+type);
    return new ConstructorValue(cls);
  }

}
