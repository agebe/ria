package org.ria.value;

import java.util.Arrays;

public class BooleanArrayValue extends AbstractArrayValue {

  private boolean[] array;

  public BooleanArrayValue(boolean[] array) {
    super();
    this.array = array;
  }

  @Override
  public Class<?> type() {
    return boolean[].class;
  }

  @Override
  public Object val() {
    return array;
  }

  @Override
  public boolean equalsOp(Value other) {
    return other instanceof BooleanArrayValue a?Arrays.equals(this.array, a.array):false;
  }

  @Override
  public Value get(int index) {
    return new BooleanValue(array[index]);
  }

  @Override
  public int length() {
    return array.length;
  }

}
