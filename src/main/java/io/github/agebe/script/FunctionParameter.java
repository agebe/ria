package io.github.agebe.script;

public class FunctionParameter implements StackItem {

  private StackItem parameter;

  public FunctionParameter(StackItem parameter) {
    super();
    this.parameter = parameter;
  }

  public StackItem getParameter() {
    return parameter;
  }

  @Override
  public String toString() {
    return "FunctionParameter [parameter=" + parameter + "]";
  }

}
