package org.rescript.run;

import java.util.List;

import org.rescript.ScriptException;
import org.rescript.expression.FunctionCall;
import org.rescript.parser.FunctionParameter;
import org.rescript.statement.Function;
import org.rescript.value.Value;

public class ScriptFunctionCaller {

  private ScriptContext ctx;

  public ScriptFunctionCaller(ScriptContext ctx) {
    super();
    this.ctx = ctx;
  }

  public Value call(FunctionCall call) {
    Function function = getFunction(call);
    if(function != null) {
      pushParamsToStack(call.getParameters());
      function.execute(ctx);
      return ctx.getStack().pop();
    } else {
      throw new ScriptException("function not found '{}'".formatted(call.getName()));
    }
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
    return getFunction(call) != null;
  }

  private Function getFunction(FunctionCall call) {
    Function current = ctx.currentFunction();
    if(current.matches(call)) {
      // recursive call
      return current;
    }
    return current.getNestedFunctions()
        .stream()
        .filter(f -> f.matches(call))
        .findFirst()
        .orElse(null);
  }

}
