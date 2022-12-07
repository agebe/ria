package org.rescript.expression;

import java.util.List;

import org.rescript.parser.FunctionParameter;
import org.rescript.run.JavaConstructorCaller;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

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
    Value v = new JavaConstructorCaller(ctx).call(type, plist);
    if(v.val() instanceof Throwable t) {
      t.setStackTrace(ctx.getStackTrace());
    }
    return v;
  }

  @Override
  public String toString() {
    return "NewOp [type=" + type + ", parameters=" + plist + "]";
  }

}
