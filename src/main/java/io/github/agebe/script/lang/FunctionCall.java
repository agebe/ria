package io.github.agebe.script.lang;

import java.util.List;

import io.github.agebe.script.FunctionCaller;

public class FunctionCall extends CachedLangItem {

  private FunctionName name;

  private List<FunctionParameter> parameters;

  // TODO this could also be a StringLiteral, probably should be LangItem
  private Identifier target;

  private FunctionCaller caller;

  public FunctionCall(
      FunctionName name,
      List<FunctionParameter> parameters,
      Identifier target,
      FunctionCaller caller) {
    super();
    this.name = name;
    this.parameters = parameters;
    this.target = target;
    this.caller = caller;
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

  @Override
  protected Result resolveOnce() {
    return caller.call(this);
  }

}
