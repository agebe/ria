package org.rescript.value;

public class FloatValue implements Value {

  private float val;

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

}
