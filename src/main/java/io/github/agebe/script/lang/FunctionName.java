package io.github.agebe.script.lang;

public class FunctionName extends CachedLangItem {

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
