package io.github.agebe.script.run;

import io.github.agebe.script.ScriptException;

public class VoidValue implements Value {

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
  public boolean isBoolean() {
    return false;
  }

  @Override
  public boolean toBoolean() {
    throw new ScriptException("void can't be converted to boolean");
  }

}
