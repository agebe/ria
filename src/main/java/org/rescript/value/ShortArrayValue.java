package org.rescript.value;

import java.util.Arrays;

public class ShortArrayValue extends AbstractArrayValue {

  private short[] array;

  public ShortArrayValue(short[] array) {
    super();
    this.array = array;
  }

  @Override
  public Class<?> type() {
    return short[].class;
  }

  @Override
  public Object val() {
    return array;
  }

  @Override
  public boolean equalsOp(Value other) {
    return other instanceof ShortArrayValue a?Arrays.equals(this.array, a.array):false;
  }

  @Override
  public Value get(int index) {
    return new ShortValue(array[index]);
  }

  @Override
  public int length() {
    return array.length;
  }

}
