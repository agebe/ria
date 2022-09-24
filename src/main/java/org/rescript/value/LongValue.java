package org.rescript.value;

public class LongValue implements Value {

  private long val;

  public LongValue(long val) {
    super();
    this.val = val;
  }

  public LongValue(Object o) {
    super();
    val = ((Number)o).longValue();
  }

  @Override
  public Class<?> type() {
    return long.class;
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
    return (int)val;
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
  public boolean isLong() {
    return true;
  }

  @Override
  public String getText() {
    return Long.toString(val);
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
    return this.val == other.toLong();
  }

}
