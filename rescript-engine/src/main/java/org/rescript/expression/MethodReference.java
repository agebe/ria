package org.rescript.expression;

import org.rescript.run.ScriptContext;
import org.rescript.value.MethodValue;
import org.rescript.value.Value;

public class MethodReference implements TargetExpression {

  private String varOrType;

  private String methodName;

  public MethodReference(String varOrType, String methodName) {
    super();
    this.varOrType = varOrType;
    this.methodName = methodName;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value v = ctx.getSymbols().resolveVarOrTypeOrStaticMember(varOrType);
    return new MethodValue(v.type(), v.val(), methodName);
  }

  @Override
  public Value eval(ScriptContext ctx, Value target) {
    Value v = ctx.getSymbols().resolveVarOrTypeOrStaticMember(target.type().getName()+"."+varOrType);
    return new MethodValue(v.type(), v.val(), methodName);
  }

}