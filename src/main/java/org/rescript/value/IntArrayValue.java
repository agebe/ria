package org.rescript.value;

import java.util.Arrays;

public class IntArrayValue implements Value, Array {

  private int[] array;

  public IntArrayValue(int[] array) {
    super();
    this.array = array;
  }

  @Override
  public Class<?> type() {
    return int[].class;
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
    return other instanceof IntArrayValue a?Arrays.equals(this.array, a.array):false;
  }

  @Override
  public Value get(int index) {
    return new IntValue(array[index]);
  }

  @Override
  public int length() {
    return array.length;
  }

}
