package org.rescript.value;

public class CharValue implements Value {

  private final char val;

  public CharValue(char val) {
    super();
    this.val = val;
  }

  public CharValue(Object o) {
    super();
    this.val = (Character)o;
  }

  @Override
  public Class<?> type() {
    return char.class;
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
    return false;
  }

  @Override
  public String getText() {
    return Character.toString(val);
  }

  @Override
  public String toString() {
    return getText();
  }

  @Override
  public boolean equalsOp(Value other) {
    return this.val == other.toInt();
  }

  @Override
  public Value inc() {
    char c = this.val;
    c+=1;
    return new CharValue(c);
  }

  @Override
  public Value dec() {
    char c = this.val;
    c-=1;
    return new CharValue(c);
  }

  @Override
  public char toChar() {
    return val;
  }

  @Override
  public boolean isChar() {
    return true;
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
