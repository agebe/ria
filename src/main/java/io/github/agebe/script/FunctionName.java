package io.github.agebe.script;

public class FunctionName implements StackItem {

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
