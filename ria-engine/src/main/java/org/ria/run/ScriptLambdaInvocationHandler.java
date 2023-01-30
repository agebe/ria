/*
 * Copyright 2023 Andre Gebers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
