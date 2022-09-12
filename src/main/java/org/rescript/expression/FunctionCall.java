package org.rescript.expression;

import java.util.List;

import org.rescript.ScriptException;
import org.rescript.parser.FunctionName;
import org.rescript.parser.FunctionParameter;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

public class FunctionCall implements TargetExpression {

  private FunctionName name;

  private List<FunctionParameter> parameters;

  public FunctionCall(
      FunctionName name,
      List<FunctionParameter> parameters,
      Expression target) {
    super();
    this.name = name;
    this.parameters = parameters;
  }

  public FunctionName getName() {
    return name;
  }

  public List<FunctionParameter> getParameters() {
    return parameters;
  }

  @Override
  public String toString() {
    return "FunctionCall [name=" + name + ", parameters=" + parameters + "]";
  }

  @Override
  public Value eval(ScriptContext ctx) {
    return ctx.getFunctions().call(this, null);
  }

  @Override
  public Value eval(ScriptContext ctx, Value target) {
    return ctx.getFunctions().call(this, target);
  }

  @Override
  public String getText() {
    throw new ScriptException("not implemented");
  }

}
