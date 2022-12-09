package org.rescript.expression;

import org.rescript.run.ScriptContext;
import org.rescript.value.ConstructorValue;
import org.rescript.value.Value;

public class ConstructorReference implements TargetExpression {

  private org.rescript.parser.Type type;

  public ConstructorReference(org.rescript.parser.Type type) {
    super();
    this.type = type;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    return new ConstructorValue(type.resolveBaseType(ctx), type.getDim());
  }

  @Override
  public Value eval(ScriptContext ctx, Value target) {
    Class<?> cls = ctx.getSymbols().getJavaSymbols().resolveType(target.type().getName()+"."+type.getName());
    return new ConstructorValue(cls, type.getDim());
  }

}
