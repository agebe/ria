package org.rescript.value;

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
  public boolean isNumber() {
    return Double.class.equals(type) ||
        Float.class.equals(type) ||
        Long.class.equals(type) ||
        Integer.class.equals(type);
  }

  @Override
  public boolean isString() {
    return String.class.equals(type);
  }

  @Override
  public boolean toBoolean() {
    return (Boolean)val;
  }

  @Override
  public double toDouble() {
    return (Double)val;
  }

  @Override
  public float toFloat() {
    return (Float)val;
  }

  @Override
  public boolean isNull() {
    return val == null;
  }

  @Override
  public int toInt() {
    return (Integer)val;
  }

  @Override
  public long toLong() {
    return (Long)val;
  }

  @Override
  public boolean isDouble() {
    return Double.class.equals(type);
  }

  @Override
  public boolean isFloat() {
    return Float.class.equals(type);
  }

  @Override
  public boolean isLong() {
    return Long.class.equals(type);
  }

  @Override
  public boolean isInteger() {
    return Integer.class.equals(type);
  }

  @Override
  public String getText() {
    return isNotNull()?val.toString():null;
  }

}
