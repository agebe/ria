package org.ria.value;

public abstract class AbstractArrayValue implements Value, Array {

  @Override
  public boolean isPrimitive() {
    return false;
  }

  public boolean isArray() {
    return true;
  }

  public Array toArray() {
    return this;
  }

}
