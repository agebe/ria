package org.rescript.value;

import org.rescript.ScriptException;

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
    throw new ScriptException("equals op not implemented");
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
