package org.rescript.parser;

import org.rescript.expression.Expression;

public class FunctionParameter implements ParseItem {

  private Expression parameter;

  public FunctionParameter(Expression parameter) {
    super();
    this.parameter = parameter;
  }

  public Expression getParameter() {
    return parameter;
  }

  @Override
  public String toString() {
    return "FunctionParameter [parameter=" + parameter + "]";
  }

}
