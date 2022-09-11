package org.rescript.value;

import java.lang.reflect.Field;

import org.rescript.ScriptException;

public class FieldValue implements Value {

  private Class<?> cls;

  private Field field;

  private Object instance;

  public FieldValue(Class<?> cls, Field field, Object instance) {
    super();
    this.cls = cls;
    this.field = field;
    this.instance = instance;
  }

  public Class<?> getCls() {
    return cls;
  }

  public Field getField() {
    return field;
  }

  @Override
  public Class<?> type() {
    return field.getType();
  }

  @Override
  public Object val() {
    try {
      return field.get(instance);
    } catch(Exception e) {
      // TODO improve exception msg
      throw new ScriptException("field get failed", e);
    }
    
  }

  @Override
  public boolean isNull() {
    return val() == null;
  }

  @Override
  public boolean isBoolean() {
    return boolean.class.equals(type()) || Boolean.class.equals(type());
  }

  @Override
  public boolean toBoolean() {
    return (boolean)val();
  }

  @Override
  public double toDouble() {
    return (double)val();
  }

  @Override
  public float toFloat() {
    return (float)val();
  }

  @Override
  public int toInt() {
    return (int)val();
  }

  @Override
  public long toLong() {
    return (long)val();
  }

  @Override
  public String toString() {
    return "FieldValue [cls=" + cls + ", field=" + field + ", instance=" + instance + "]";
  }

}
