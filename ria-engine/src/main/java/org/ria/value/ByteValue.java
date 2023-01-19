package org.ria.value;

public class ByteValue implements Value {

  private final byte val;

  public ByteValue(byte val) {
    super();
    this.val = val;
  }

  public ByteValue(Object o) {
    this(((Number)o).byteValue());
  }

  @Override
  public Class<?> type() {
    return byte.class;
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
    return this.val == other.toByte();
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
    return val;
  }

  @Override
  public short toShort() {
    return val;
  }

  @Override
  public boolean isByte() {
    return true;
  }

  @Override
  public boolean isShort() {
    return false;
  }

  @Override
  public Value inc() {
    return new ByteValue(this.val+1);
  }

  @Override
  public Value dec() {
    return new ByteValue(this.val-1);
  }

}
