package org.rescript.value;

import java.util.Arrays;

public class DoubleArrayValue implements Value, Array {

  private double[] array;

  public DoubleArrayValue(double[] array) {
    super();
    this.array = array;
  }

  @Override
  public Class<?> type() {
    return double[].class;
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
    return other instanceof DoubleArrayValue a?Arrays.equals(this.array, a.array):false;
  }

  @Override
  public Value get(int index) {
    return new DoubleValue(array[index]);
  }

  @Override
  public int length() {
    return array.length;
  }

}
