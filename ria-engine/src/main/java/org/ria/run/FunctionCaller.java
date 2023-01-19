package org.ria.run;

import org.ria.ScriptException;
import org.ria.expression.FunctionCall;
import org.ria.symbol.VarSymbol;
import org.ria.value.ConstructorValue;
import org.ria.value.Value;
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
      ConstructorValue c = varSym.get().toConstructorValue();
      if(c.getDim() == 0) {
        return javaConstructorCaller.call(c.getTargetType(), function.getParameters());
      } else {
        throw new ScriptException(
            "internal error, array construction not handled here. type '%s',  dim '%s', function '%s'"
            .formatted(c.getTargetType(), c.getDim(), function));
      }
    } else if((varSym != null) && varSym.getVal().isMethod()) {
      return javaFunctionCaller.call(varSym.getVal().toMethodValue(), function);
    } else {
      VarSymbol functionSymbol = ctx.getSymbols()
          .getScriptSymbols()
          .getCurrentScope()
          .getFunctionSymbol(function.getName().getName());
      if(functionSymbol != null) {
        return javaFunctionCaller.call(functionSymbol.getVal().toMethodValue(), function);
      } else if(target != null) {
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
