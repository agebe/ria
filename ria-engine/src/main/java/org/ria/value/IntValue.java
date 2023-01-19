package org.ria.value;

public class IntValue implements Value {

  private final int val;

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
  public byte toByte() {
    return (byte)val;
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
  public String getText() {
    return Integer.toString(val);
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
    return this.val == other.toInt();
  }

  @Override
  public Value inc() {
    return new IntValue(this.val+1);
  }

  @Override
  public Value dec() {
    return new IntValue(this.val-1);
  }

  @Override
  public char toChar() {
    return (char)val;
  }

  @Override
  public short toShort() {
    return (short)val;
  }

}
