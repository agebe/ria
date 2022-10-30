package org.rescript.value;

import java.util.Arrays;

public class FloatArrayValue implements Value, Array {

  private float[] array;

  public FloatArrayValue(float[] array) {
    super();
    this.array = array;
  }

  @Override
  public Class<?> type() {
    return float[].class;
  }

  @Override
  public Object val() {
    return array;
  }

  @Override
  public boolean isPrimitive() {
    return false;
  }

  @Override
  public boolean equalsOp(Value other) {
    return other instanceof FloatArrayValue a?Arrays.equals(this.array, a.array):false;
  }

  @Override
  public Value get(int index) {
    return new FloatValue(array[index]);
  }

  @Override
  public int length() {
    return array.length;
  }

}
