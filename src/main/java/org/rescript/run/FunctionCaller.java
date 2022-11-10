package org.rescript.run;

import org.rescript.expression.FunctionCall;
import org.rescript.symbol.VarSymbol;
import org.rescript.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionCaller {

  private static final Logger log = LoggerFactory.getLogger(FunctionCaller.class);

  private JavaConstructorCaller javaConstructorCaller;

  private JavaFunctionCaller javaFunctionCaller;

  private ScriptFunctionCaller scriptFunctionCaller;

  private ScriptContext ctx;

  public FunctionCaller(ScriptContext ctx) {
    super();
    this.ctx = ctx;
    this.javaConstructorCaller = new JavaConstructorCaller(ctx);
    this.javaFunctionCaller = new JavaFunctionCaller(ctx);
    this.scriptFunctionCaller = new ScriptFunctionCaller(ctx);
  }

  public Value call(FunctionCall function, Value target) {
    log.debug("call function '{}' on target '{}'", function, target);
    VarSymbol varSym = ctx.getSymbols().getScriptSymbols().resolveVar(function.getName().getName());
    if((varSym != null) && varSym.getVal().isConstructor()) {
      return javaConstructorCaller.call(varSym.get().toConstructorValue().getTargetType(), function.getParameters());
    } else if((varSym != null) && varSym.getVal().isMethod()) {
      return javaFunctionCaller.call(varSym.getVal().toMethodValue(), function);
    } else {
      if(target != null) {
        // no own types currently in the script language so has to be a java function
        return javaFunctionCaller.call(function, target);
      } else {
        if(scriptFunctionCaller.hasFunction(function)) {
          return scriptFunctionCaller.call(function);
        } else {
          return javaFunctionCaller.call(function, target);
        }
      }
    }
  }

}
