package org.ria.parser;

public class FunctionName implements ParseItem {

  private String name;

  public FunctionName(String name) {
    super();
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "FunctionName [name=" + name + "]";
  }

}
