package org.rescript.value;

import java.util.Arrays;

public class ByteArrayValue extends AbstractArrayValue {

  private byte[] array;

  public ByteArrayValue(byte[] array) {
    super();
    this.array = array;
  }

  @Override
  public Class<?> type() {
    return byte[].class;
  }

  @Override
  public Object val() {
    return array;
  }

  @Override
  public boolean equalsOp(Value other) {
    return other instanceof ByteArrayValue a?Arrays.equals(this.array, a.array):false;
  }

  @Override
  public Value get(int index) {
    return new ByteValue(array[index]);
  }

  @Override
  public int length() {
    return array.length;
  }

}
