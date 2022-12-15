package org.rescript.parser;

import java.util.List;

public class FunctionParameters implements ParseItem {

  private List<FunctionParameter> parameters;

  public FunctionParameters(List<FunctionParameter> parameters) {
    super();
    this.parameters = parameters;
  }

  public List<FunctionParameter> getParameters() {
    return parameters;
  }

  @Override
  public String toString() {
    return "FunctionParameters [parameters=" + parameters + "]";
  }

}
