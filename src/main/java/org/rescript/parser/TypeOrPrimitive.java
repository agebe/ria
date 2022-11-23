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

  public boolean isPrimitive() {
    return isDouble() ||
        isFloat() ||
        isLong() ||
        isInt() ||
        isBoolean() ||
        isByte() ||
        isChar() ||
        isShort() ||
        isVoid();
  }

  public boolean isDouble() {
    return "double".equals(type);
  }

  public boolean isFloat() {
    return "float".equals(type);
  }

  public boolean isLong() {
    return "long".equals(type);
  }

  public boolean isInt() {
    return "int".equals(type);
  }

  public boolean isBoolean() {
    return "boolean".equals(type);
  }

  public boolean isShort() {
    return "short".equals(type);
  }

  public boolean isChar() {
    return "char".equals(type);
  }

  public boolean isByte() {
    return "byte".equals(type);
  }

  public boolean isVoid() {
    return "void".equals(type);
  }

}
