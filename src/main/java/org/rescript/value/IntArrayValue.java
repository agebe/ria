package org.rescript.value;

import org.rescript.ScriptException;

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
    throw new ScriptException("equals op not implemented");
  }

  @Override
  public Value get(int index) {
    return new IntValue(array[index]);
  }

}
