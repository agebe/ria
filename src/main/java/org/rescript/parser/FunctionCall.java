package org.rescript.parser;

import java.util.List;

import org.rescript.ScriptException;
import org.rescript.run.Expressions;
import org.rescript.run.Value;

public class FunctionCall implements ParseItem, TargetExpression {

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
  public Value eval(Expressions expressions) {
    return expressions.getFunctions().call(this, null);
  }

  @Override
  public Value eval(Expressions expressions, Value target) {
    return expressions.getFunctions().call(this, target);
  }

  @Override
  public String getText() {
    throw new ScriptException("not implemented");
  }

}
