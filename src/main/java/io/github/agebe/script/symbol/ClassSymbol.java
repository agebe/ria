package io.github.agebe.script.symbol;

public class ClassSymbol implements Symbol {

  private Class<?> cls;

  public ClassSymbol(Class<?> cls) {
    super();
    this.cls = cls;
  }

  public Class<?> getCls() {
    return cls;
  }

  @Override
  public String toString() {
    return "ClassSymbol [cls=" + cls + "]";
  }

}
