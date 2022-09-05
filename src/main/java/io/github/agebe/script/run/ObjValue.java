package io.github.agebe.script.run;

import io.github.agebe.script.ScriptException;

public class ObjValue implements Value {

  private Class<?> type;

  private Object val;

  public ObjValue(Class<?> type, Object val) {
    super();
    this.type = type;
    this.val = val;
  }

  public Class<?> getType() {
    return type;
  }

  public Object getVal() {
    return val;
  }

  @Override
  public Class<?> type() {
    return getType();
  }

  @Override
  public Object val() {
    return getVal();
  }

  @Override
  public String toString() {
    return "ObjValue [type=" + type + "]";
  }

  @Override
  public boolean isBoolean() {
    return Boolean.class.equals(type);
  }

  @Override
  public boolean toBoolean() {
    if(isBoolean()) {
      return (Boolean)val;
    } else {
      throw new ScriptException("not boolean but '%s'".formatted(type));
    }
  }

  @Override
  public boolean isNull() {
    return val == null;
  }

}
