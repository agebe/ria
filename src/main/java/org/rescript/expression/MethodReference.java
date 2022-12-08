package org.rescript.expression;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.rescript.run.ScriptContext;
import org.rescript.symbol.SymbolNotFoundException;
import org.rescript.symbol.java.RUtils;
import org.rescript.util.ExceptionUtils;
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

  private void checkMethodExists(Value v, ScriptContext ctx) {
    List<Method> l = RUtils.findAccessibleMethods(v.type(), v.val(), methodName);
    if(l.isEmpty()) {
      throw ExceptionUtils.replaceStackTrace(new SymbolNotFoundException(
          "could not find method '%s' on type '%s', %s".formatted(
              methodName,
              v.type().getName(),
              v.val()!=null?"on object "+ObjectUtils.identityToString(v.val()):"static")), ctx);
    }
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value v = ctx.getSymbols().resolveVarOrTypeOrStaticMember(varOrType);
    checkMethodExists(v, ctx);
    return new MethodValue(v.type(), v.val(), methodName);
  }

  @Override
  public Value eval(ScriptContext ctx, Value target) {
    Value v = ctx.getSymbols().resolveVarOrTypeOrStaticMember(target.type().getName()+"."+varOrType);
    checkMethodExists(v, ctx);
    return new MethodValue(v.type(), v.val(), methodName);
  }

}
