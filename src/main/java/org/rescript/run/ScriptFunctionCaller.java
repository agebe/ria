package org.rescript.run;

import java.util.List;

import org.rescript.ScriptException;
import org.rescript.expression.FunctionCall;
import org.rescript.parser.FunctionParameter;
import org.rescript.statement.Function;
import org.rescript.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptFunctionCaller {

  private static final Logger log = LoggerFactory.getLogger(ScriptFunctionCaller.class);

  private ScriptContext ctx;

  public ScriptFunctionCaller(ScriptContext ctx) {
    super();
    this.ctx = ctx;
  }

  public Value call(FunctionCall call) {
    Function function = findFunction(call);
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
    return findFunction(call) != null;
  }

  private Function findFunction(FunctionCall call) {
    log.debug("find function '%s'".formatted(call));
    Function current = ctx.currentFunction();
    log.debug("check current");
    if(current.matches(call)) {
      // recursive call
      log.debug("is current");
      return current;
    }
    log.debug("check nested");
    Function nested = current.getNestedFunctions()
        .stream()
        .filter(f -> f.matches(call))
        .findFirst()
        .orElse(null);
    if(nested != null) {
      log.debug("is nested");
      return nested;
    }
    log.debug("check parent");
    Function parent = current.getParent();
    if(parent != null) {
      if(parent.matches(call)) {
        log.debug("is parent");
        return parent;
      } else {
        log.debug("check sibling");
        Function sibling = parent.getNestedFunctions()
            .stream()
            .filter(f -> f.matches(call))
            .findFirst()
            .orElse(null);
        if(sibling != null) {
          return sibling;
        }
      }
    }
    // TODO should we go up the tree all the way to the root to find the method?
    log.debug("script function not found");
    return null;
  }

}
