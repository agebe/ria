package org.rescript.run;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.rescript.value.FunctionValue;
import org.rescript.value.Value;

public class ScriptLambdaInvocationHandler implements InvocationHandler {

  private FunctionValue function;

  private ScriptContext ctx;

  public ScriptLambdaInvocationHandler(FunctionValue function, ScriptContext ctx) {
    super();
    this.function = function;
    this.ctx = ctx;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Value v = new ScriptFunctionCaller(ctx).call(function, args);
    // TODO might want to take control over converting value to method.getReturnType()?
    return v.val();
  }

}
