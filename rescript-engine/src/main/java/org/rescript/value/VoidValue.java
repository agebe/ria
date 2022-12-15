package org.rescript.value;

import org.rescript.ScriptException;

public class VoidValue implements Value {

  public static final VoidValue VOID = new VoidValue();

  private VoidValue() {
    super();
  }

  @Override
  public Class<?> type() {
    return Void.class;
  }

  @Override
  public Object val() {
    return null;
  }

  @Override
  public boolean isNull() {
    return true;
  }

  @Override
  public boolean toBoolean() {
    throw new ScriptException("void can't be cast to boolean");
  }

  @Override
  public double toDouble() {
    throw new ScriptException("void can't be cast to double");
  }

  @Override
  public float toFloat() {
    throw new ScriptException("void can't be cast to float");
  }

  @Override
  public int toInt() {
    throw new ScriptException("void can't be cast to int");
  }

  @Override
  public long toLong() {
    throw new ScriptException("void can't be cast to long");
  }

  @Override
  public boolean isPrimitive() {
    return true;
  }

  @Override
  public boolean equalsOp(Value other) {
    throw new ScriptException("no equalsOp on void");
  }

  @Override
  public Value unbox() {
    throw new ScriptException("can't unbox void");
  }

}
