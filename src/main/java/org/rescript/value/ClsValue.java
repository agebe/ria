package org.rescript.value;

// avoid clash with java.lang.ClassValue
public class ClsValue implements Value {

  private Class<?> cls;

  public ClsValue(Class<?> cls) {
    super();
    this.cls = cls;
  }

  @Override
  public Class<?> type() {
    return cls;
  }

  @Override
  public Object val() {
    return null;
  }

  @Override
  public String toString() {
    return "ClassValue [cls=" + cls + "]";
  }

}
