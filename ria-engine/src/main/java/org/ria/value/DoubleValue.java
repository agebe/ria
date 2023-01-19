package org.ria.value;

import org.ria.ScriptException;

public class DoubleValue implements Value {

  private final double val;

  public DoubleValue(Object o) {
    super();
    val = ((Number)o).doubleValue();
  }

  public DoubleValue(double val) {
    super();
    this.val = val;
  }

  @Override
  public Class<?> type() {
    return double.class;
  }

  @Override
  public Object val() {
    return val;
  }

  @Override
  public boolean isNull() {
    return false;
  }

  @Override
  public boolean toBoolean() {
    throw new ScriptException("double can't be cast to boolean");
  }

  @Override
  public double toDouble() {
    return val;
  }

  @Override
  public float toFloat() {
    return (float)val;
  }

  @Override
  public int toInt() {
    return (int)val;
  }

  @Override
  public long toLong() {
    return (long)val;
  }

  @Override
  public boolean isNumber() {
    return true;
  }

  @Override
  public boolean isDouble() {
    return true;
  }

  @Override
  public String getText() {
    return Double.toString(val);
  }

  @Override
  public String toString() {
    return getText();
  }

  @Override
  public boolean isPrimitive() {
    return true;
  }

  @Override
  public boolean equalsOp(Value other) {
    return this.val == other.toDouble();
  }

  @Override
  public Value inc() {
    return new DoubleValue(this.val+1);
  }

  @Override
  public Value dec() {
    return new DoubleValue(this.val-1);
  }

  @Override
  public byte toByte() {
    return (byte)val;
  }

  @Override
  public short toShort() {
    return (short)val;
  }

}
