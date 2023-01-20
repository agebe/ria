package org.ria.run;

import org.ria.ScriptException;
import org.ria.expression.FunctionCall;
import org.ria.symbol.VarSymbol;
import org.ria.value.ConstructorValue;
import org.ria.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionInvoker {

  private static final Logger log = LoggerFactory.getLogger(FunctionInvoker.class);

  private JavaConstructorInvoker javaConstructorInvoker;

  private JavaMethodInvoker javaMethodInvoker;

  private ScriptFunctionInvoker scriptFunctionInvoker;

  private ScriptContext ctx;

  public FunctionInvoker(ScriptContext ctx) {
    super();
    this.ctx = ctx;
    this.javaConstructorInvoker = new JavaConstructorInvoker(ctx);
    this.javaMethodInvoker = new JavaMethodInvoker(ctx);
    this.scriptFunctionInvoker = new ScriptFunctionInvoker(ctx);
  }

  public Value call(FunctionCall function, Value target) {
    log.debug("call function '{}' on target '{}'", function, target);
    VarSymbol varSym = ctx.getSymbols().getScriptSymbols().resolveVar(function.getName().getName());
    if((varSym != null) && varSym.getVal().isConstructor()) {
      ConstructorValue c = varSym.get().toConstructorValue();
      if(c.getDim() == 0) {
        return javaConstructorInvoker.invoke(c.getTargetType(), function.getParameters());
      } else {
        throw new ScriptException(
            "internal error, array construction not handled here. type '%s',  dim '%s', function '%s'"
            .formatted(c.getTargetType(), c.getDim(), function));
      }
    } else if((varSym != null) && varSym.getVal().isMethod()) {
      return javaMethodInvoker.invoke(varSym.getVal().toMethodValue(), function);
    } else {
      VarSymbol functionSymbol = ctx.getSymbols()
          .getScriptSymbols()
          .getCurrentScope()
          .getFunctionSymbol(function.getName().getName());
      if(functionSymbol != null) {
        return javaMethodInvoker.invoke(functionSymbol.getVal().toMethodValue(), function);
      } else if(target != null) {
        // no own types currently in the script language so has to be a java function
        return javaMethodInvoker.invoke(function, target);
      } else {
        if(scriptFunctionInvoker.hasFunction(function)) {
          return scriptFunctionInvoker.call(function);
        } else {
          return javaMethodInvoker.invoke(function, target);
        }
      }
    }
  }

}
