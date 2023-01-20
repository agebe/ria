package org.ria.run;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.ria.symbol.script.ScopeNode;
import org.ria.value.FunctionValue;
import org.ria.value.Value;

public class ScriptLambdaInvocationHandler implements InvocationHandler {

  private FunctionValue function;

  private ScriptContext ctx;

  private ScopeNode scope;

  public ScriptLambdaInvocationHandler(FunctionValue function, ScriptContext ctx) {
    super();
    this.function = function;
    this.ctx = ctx;
    this.scope = ctx.getSymbols().getScriptSymbols().getCurrentScope();
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // setup the scope so the lambda can access outer variables.
    // this should only happen when the lambda is executed by a different thread
    // than it was created with.
    if(ctx.getSymbols().getScriptSymbols().getCurrentScope() != scope) {
      ctx.getSymbols().getScriptSymbols().setCurrentScope(scope);
    }
    Value v = new ScriptFunctionInvoker(ctx).invoke(function, args);
    // TODO might want to take control over converting value to method.getReturnType()?
    return v.val();
  }

}
