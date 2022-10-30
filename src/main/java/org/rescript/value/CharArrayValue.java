package org.rescript.value;

import java.util.Arrays;

public class CharArrayValue implements Value, Array {

  private char[] array;

  public CharArrayValue(char[] array) {
    super();
    this.array = array;
  }

  @Override
  public Class<?> type() {
    return char[].class;
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
    return other instanceof CharArrayValue a?Arrays.equals(this.array, a.array):false;
  }

  @Override
  public Value get(int index) {
    return new CharValue(array[index]);
  }

  @Override
  public int length() {
    return array.length;
  }


}
