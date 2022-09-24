package org.rescript.value;

import org.rescript.ScriptException;

public class DoubleValue implements Value {

  private double val;

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


}
