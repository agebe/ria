package org.rescript.value;

import java.lang.reflect.Constructor;

public class ConstructorValue implements Value {

  private Class<?> targetType;

  public ConstructorValue(Class<?> targetType) {
    super();
    this.targetType = targetType;
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

  @Override
  public boolean isConstructor() {
    return true;
  }

  @Override
  public ConstructorValue toConstructorValue() {
    return this;
  }

}
