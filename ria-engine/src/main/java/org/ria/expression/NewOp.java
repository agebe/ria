package org.ria.expression;

import java.util.List;

import org.ria.parser.FunctionParameter;
import org.ria.run.JavaConstructorInvoker;
import org.ria.run.ScriptContext;
import org.ria.util.ExceptionUtils;
import org.ria.value.Value;

public class NewOp implements Expression {

  private String type;

  private List<FunctionParameter> plist;

  public NewOp(String type, List<FunctionParameter> parameters) {
    super();
    this.type = type;
    this.plist = parameters;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value v = new JavaConstructorInvoker(ctx).invoke(type, plist);
    if(v.val() instanceof Throwable t) {
      ExceptionUtils.fixStackTrace(t, ctx);
    }
    return v;
  }

  @Override
  public String toString() {
    return "NewOp [type=" + type + ", parameters=" + plist + "]";
  }

}
