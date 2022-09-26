package org.rescript.value;

import org.rescript.ScriptException;

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

  @Override
  public boolean isPrimitive() {
    return false;
  }

  @Override
  public boolean equalsOp(Value other) {
    return this.cls == ((ClsValue)other).cls;
  }

  @Override
  public Value unbox() {
    throw new ScriptException("can't unbox class");
  }

}
