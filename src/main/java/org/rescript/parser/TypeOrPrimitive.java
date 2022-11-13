package org.rescript.parser;

public class TypeOrPrimitive implements ParseItem {

  private String type;

  public TypeOrPrimitive(String type) {
    super();
    this.type = type;
  }

  public String getType() {
    return type;
  }

}
