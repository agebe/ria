package org.ria.value;

import java.lang.reflect.Constructor;

public class ConstructorValue implements Value {

  private Class<?> targetType;

  private int dim;

  public ConstructorValue(Class<?> targetType, int dim) {
    super();
    this.targetType = targetType;
    this.dim = dim;
  }

  @Override
  public Class<?> type() {
    return Constructor.class;
  }

  @Override
  public String typeOf() {
    return "constructor";
  }

  @Override
  public Object val() {
    return null;
  }

  @Override
  public boolean isPrimitive() {
    return false;
  }

  @Override
  public boolean equalsOp(Value other) {
    // TODO Auto-generated method stub
    return false;
  }

  public Class<?> getTargetType() {
    return targetType;
  }

  public int getDim() {
    return dim;
  }

  @Override
  public boolean isConstructor() {
    return true;
  }

  @Override
  public ConstructorValue toConstructorValue() {
    return this;
  }

}
