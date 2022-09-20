package org.rescript.value;

public class IntValue implements Value {

  private int val;

  public IntValue(int val) {
    super();
    this.val = val;
  }

  public IntValue(Object o) {
    super();
    val = ((Number)o).intValue();
  }

  @Override
  public Class<?> type() {
    return int.class;
  }

  @Override
  public Object val() {
    return val;
  }

  @Override
  public double toDouble() {
    return val;
  }

  @Override
  public float toFloat() {
    return val;
  }

  @Override
  public int toInt() {
    return val;
  }

  @Override
  public long toLong() {
    return val;
  }

  @Override
  public boolean isNumber() {
    return true;
  }

  @Override
  public boolean isInteger() {
    return true;
  }

  @Override
  public String toString() {
    return Integer.toString(val);
  }

}
