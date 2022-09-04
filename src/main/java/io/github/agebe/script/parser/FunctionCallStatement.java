package io.github.agebe.script.parser;

public class FunctionCallStatement implements Statement {

  private FunctionCall function;

  public FunctionCallStatement(FunctionCall function) {
    super();
    this.function = function;
  }

  public FunctionCall getFunction() {
    return function;
  }

  @Override
  public String toString() {
    return "FunctionCallStatement [function=" + function + "]";
  }

}
