package org.rescript.value;

public abstract class EvaluatedFromValue implements Value {

  protected abstract Value getWrapped();

  @Override
  public Class<?> type() {
    return getWrapped().type();
  }

  @Override
  public Object val() {
    return getWrapped().val();
  }

  @Override
  public boolean isNull() {
    return getWrapped().isNull();
  }

  @Override
  public boolean isBoolean() {
    return getWrapped().isBoolean();
  }

  @Override
  public boolean isNumber() {
    return getWrapped().isNumber();
  }

  @Override
  public boolean isDouble() {
    return getWrapped().isDouble();
  }

  @Override
  public boolean isFloat() {
    return getWrapped().isFloat();
  }

  @Override
  public boolean isLong() {
    return getWrapped().isLong();
  }

  @Override
  public boolean isInteger() {
    return getWrapped().isInteger();
  }

  @Override
  public boolean isString() {
    return getWrapped().isString();
  }

  @Override
  public boolean isPrimitive() {
    return getWrapped().isPrimitive();
  }

  @Override
  public boolean equalsOp(Value other) {
    return getWrapped().equalsOp(other);
  }

  @Override
  public String getText() {
    return getWrapped().getText();
  }

  @Override
  public boolean toBoolean() {
    return getWrapped().toBoolean();
  }

  @Override
  public double toDouble() {
    return getWrapped().toDouble();
  }

  @Override
  public float toFloat() {
    return getWrapped().toFloat();
  }

  @Override
  public int toInt() {
    return getWrapped().toInt();
  }

  @Override
  public long toLong() {
    return getWrapped().toLong();
  }

  @Override
  public Value unbox() {
    return getWrapped().unbox();
  }

}
