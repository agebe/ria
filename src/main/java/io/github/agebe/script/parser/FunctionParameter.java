package io.github.agebe.script.parser;

public class FunctionParameter implements ParseItem {

  private ParseItem parameter;

  public FunctionParameter(ParseItem parameter) {
    super();
    this.parameter = parameter;
  }

  public ParseItem getParameter() {
    return parameter;
  }

  @Override
  public String toString() {
    return "FunctionParameter [parameter=" + parameter + "]";
  }

}
