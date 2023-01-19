package org.ria.expression;

import org.ria.run.ScriptContext;
import org.ria.value.ConstructorValue;
import org.ria.value.Value;

public class ConstructorReference implements TargetExpression {

  private org.ria.parser.Type type;

  public ConstructorReference(org.ria.parser.Type type) {
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
