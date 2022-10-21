package org.rescript.run;

import org.rescript.expression.FunctionCall;
import org.rescript.value.Value;

public class FunctionCaller {

  private JavaFunctionCaller javaFunctionCaller;

  private ScriptFunctionCaller scriptFunctionCaller;

  public FunctionCaller(ScriptContext ctx) {
    super();
    this.javaFunctionCaller = new JavaFunctionCaller(ctx);
    this.scriptFunctionCaller = new ScriptFunctionCaller(ctx);
  }

  public Value call(FunctionCall function, Value target) {
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
