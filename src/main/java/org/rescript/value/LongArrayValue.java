package org.rescript.value;

import java.util.Arrays;

public class LongArrayValue implements Value, Array {

  private long[] array;

  public LongArrayValue(long[] array) {
    super();
    this.array = array;
  }

  @Override
  public Class<?> type() {
    return long[].class;
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
    return other instanceof LongArrayValue a?Arrays.equals(this.array, a.array):false;
  }

  @Override
  public Value get(int index) {
    return new LongValue(array[index]);
  }

  @Override
  public int length() {
    return array.length;
  }

}
