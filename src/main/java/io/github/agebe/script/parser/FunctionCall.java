package io.github.agebe.script.parser;

import java.util.List;

public class FunctionCall implements ParseItem {

  private FunctionName name;

  private List<FunctionParameter> parameters;

  // TODO this could also be a StringLiteral, probably should be LangItem
  private Identifier target;

  public FunctionCall(
      FunctionName name,
      List<FunctionParameter> parameters,
      Identifier target) {
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

  public Identifier getTarget() {
    return target;
  }

  @Override
  public String toString() {
    return "FunctionCall [name=" + name + ", parameters=" + parameters + ", target=" + target + "]";
  }

}
