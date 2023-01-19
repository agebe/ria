package org.ria.value;

public class FloatValue implements Value {

  private final float val;

  public FloatValue(Object o) {
    super();
    val = ((Number)o).floatValue();
  }

  public FloatValue(float val) {
    super();
    this.val = val;
  }

  @Override
  public Class<?> type() {
    return float.class;
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
    return (long)val;
  }

  @Override
  public boolean isNumber() {
    return true;
  }

  @Override
  public boolean isFloat() {
    return true;
  }

  @Override
  public String getText() {
    return Float.toString(val);
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
    return this.val == other.toFloat();
  }

  @Override
  public Value inc() {
    return new FloatValue(this.val+1);
  }

  @Override
  public Value dec() {
    return new FloatValue(this.val-1);
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
