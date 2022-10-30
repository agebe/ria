package org.rescript.value;

import org.rescript.ScriptException;

public class ArrayValue implements Value, Array {

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
  public boolean isPrimitive() {
    return false;
  }

  @Override
  public boolean equalsOp(Value other) {
    throw new ScriptException("equals op not implemented");
  }

  @Override
  public Value get(int index) {
    return Value.of(array[index]);
  }

}
