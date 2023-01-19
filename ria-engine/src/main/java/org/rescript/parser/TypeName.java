package org.rescript.parser;

public class TypeName implements ParseItem {

  private String name;

  public TypeName(String name) {
    super();
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "TypeName [name=" + name + "]";
  }

}
