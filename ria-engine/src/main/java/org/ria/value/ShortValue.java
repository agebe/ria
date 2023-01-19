package org.ria.value;

public class ShortValue implements Value {

  private final short val;

  public ShortValue(short val) {
    super();
    this.val = val;
  }

  public ShortValue(Object o) {
    this(((Number)o).shortValue());
  }

  @Override
  public Class<?> type() {
    return short.class;
  }

  @Override
  public Object val() {
    return val;
  }

  @Override
  public boolean isPrimitive() {
    return true;
  }

  @Override
  public boolean equalsOp(Value other) {
    return this.val == other.toShort();
  }

  @Override
  public boolean isNumber() {
    return true;
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
  public char toChar() {
    return (char)val;
  }

  @Override
  public byte toByte() {
    return (byte)val;
  }

  @Override
  public short toShort() {
    return val;
  }

  @Override
  public boolean isByte() {
    return false;
  }

  @Override
  public boolean isShort() {
    return true;
  }

  @Override
  public Value inc() {
    return new ShortValue(this.val+1);
  }

  @Override
  public Value dec() {
    return new ShortValue(this.val-1);
  }

}
