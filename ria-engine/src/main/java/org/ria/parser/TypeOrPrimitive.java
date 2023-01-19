package org.ria.parser;

public class TypeOrPrimitive implements ParseItem {

  private Type type;

  public TypeOrPrimitive() {
    this(new Type());
  }

  public TypeOrPrimitive(Type type) {
    super();
    this.type = type;
  }

  public Type getType() {
    return type;
  }

}
