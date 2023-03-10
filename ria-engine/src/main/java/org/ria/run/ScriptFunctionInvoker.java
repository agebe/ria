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

import java.util.List;

import org.ria.ScriptException;
import org.ria.expression.FunctionCall;
import org.ria.parser.FunctionParameter;
import org.ria.statement.Function;
import org.ria.symbol.SymbolNotFoundException;
import org.ria.symbol.VarSymbol;
import org.ria.value.FunctionValue;
import org.ria.value.Value;

public class ScriptFunctionInvoker {

  private ScriptContext ctx;

  public ScriptFunctionInvoker(ScriptContext ctx) {
    super();
    this.ctx = ctx;
  }

  public Value call(FunctionCall call) {
    Function function = findFunction(call);
    if(function != null) {
      pushParamsToStack(call.getParameters());
      function.executeFunction(ctx);
      return ctx.getStack().pop();
    } else {
      throw new ScriptException("function not found '{}'".formatted(call.getName()));
    }
  }

  public Value invoke(FunctionValue val, Object[] args) {
    // TODO find function if there are multiple
    Function function = val.getFunctions().get(0);
    if(args != null) {
      for(Object o : args) {
        ctx.getStack().push(Value.of(o));
      }
    }
    function.executeFunction(ctx);
    return ctx.getStack().pop();
  }

  private void pushParamsToStack(List<FunctionParameter> parameters) {
    parameters
    .stream()
    .map(p -> {
      Value val = p.getParameter().eval(ctx);
      if(val == null) {
        throw new ScriptException("parameter evaluated to null, '%s'".formatted(p));
      }
      return val;
    })
    .forEach(val -> ctx.getStack().push(val));
  }

  public boolean hasFunction(FunctionCall call) {
    return findFunction(call) != null;
  }

  private Function findFunction(FunctionCall call) {
    Function function = findFunctionVariable(call);
    if(function != null) {
      return function;
    }
    return ctx.getSymbols().getScriptSymbols().findFunctions(call.getName().getName())
        .stream()
        .filter(f -> f.matches(call))
        .findFirst()
        .orElse(null);
  }

  @SuppressWarnings("unchecked")
  private Function findFunctionVariable(FunctionCall call) {
    try {
      VarSymbol var = ctx.getSymbols().getScriptSymbols().resolveVar(call.getName().getName());
      if(var != null) {
        Value v = var.getVal();
        if(v.isFunction()) {
          return ((List<Function>)v.val())
              .stream()
              .filter(f -> f.matchesParameters(call))
              .findFirst()
              .orElse(null);
        }
      }
    } catch(SymbolNotFoundException e) {
    }
    return null;
  }

}
