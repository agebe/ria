package org.rescript.value;

import org.rescript.ScriptException;

public class BooleanValue implements Value {

  public static final BooleanValue TRUE = new BooleanValue(true);
  public static final BooleanValue FALSE = new BooleanValue(false);

  private boolean val;

  public BooleanValue(Object o) {
    super();
    val = (Boolean)o;
  }

  public BooleanValue(boolean val) {
    super();
    this.val = val;
  }

  @Override
  public Class<?> type() {
    return boolean.class;
  }

  @Override
  public Object val() {
    return val;
  }

  @Override
  public String toString() {
    return "BooleanValue [val=" + val + "]";
  }

  @Override
  public boolean isBoolean() {
    return true;
  }

  @Override
  public boolean toBoolean() {
    return val;
  }

  @Override
  public double toDouble() {
    throw new ScriptException("boolean can't be cast to double");
  }

  @Override
  public float toFloat() {
    throw new ScriptException("boolean can't be cast to float");
  }

  @Override
  public int toInt() {
    throw new ScriptException("boolean can't be cast to int");
  }

  @Override
  public long toLong() {
    throw new ScriptException("boolean can't be cast to long");
  }

  @Override
  public String getText() {
    return val?"true":"false";
  }

  @Override
  public boolean isPrimitive() {
    return true;
  }

  public static BooleanValue valueOf(boolean bool) {
    return bool?BooleanValue.TRUE:BooleanValue.FALSE;
  }

  @Override
  public boolean equalsOp(Value other) {
    return this.val == other.toBoolean();
  }

  public static BooleanValue of(boolean b) {
    return b?BooleanValue.TRUE:BooleanValue.FALSE;
  }

}
