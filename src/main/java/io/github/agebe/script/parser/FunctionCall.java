package io.github.agebe.script.parser;

import java.util.List;

import io.github.agebe.script.run.Expressions;
import io.github.agebe.script.run.Value;

public class FunctionCall implements ParseItem, Expression {

  private FunctionName name;

  private List<FunctionParameter> parameters;

  private Expression target;

  public FunctionCall(
      FunctionName name,
      List<FunctionParameter> parameters,
      Expression target) {
    super();
    this.name = name;
    this.parameters = parameters;
    this.target = target;
  }

  public FunctionName getName() {
    return name;
  }

  public List<FunctionParameter> getParameters() {
    return parameters;
  }

  public Expression getTarget() {
    return target;
  }

  @Override
  public String toString() {
    return "FunctionCall [name=" + name + ", parameters=" + parameters + ", target=" + target + "]";
  }

  @Override
  public Value eval(Expressions expressions) {
    return expressions.getFunctions().call(this);
  }

}
