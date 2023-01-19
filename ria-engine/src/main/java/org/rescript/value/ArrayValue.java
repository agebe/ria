package org.rescript.value;

import java.util.Arrays;

public class ArrayValue extends AbstractArrayValue {

  private Object[] array;

  private Class<?> type;

  public ArrayValue(Object[] array, Class<?> type) {
    super();
    this.array = array;
    this.type = type;
  }

  @Override
  public Class<?> type() {
    return type;
  }

  @Override
  public Object val() {
    return array;
  }

  @Override
  public boolean equalsOp(Value other) {
    return other instanceof ArrayValue a?Arrays.equals(this.array, a.array):false;
  }

  @Override
  public Value get(int index) {
    return Value.of(array[index]);
  }

  @Override
  public int length() {
    return array.length;
  }

}
