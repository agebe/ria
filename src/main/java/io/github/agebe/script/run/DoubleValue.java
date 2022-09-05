package io.github.agebe.script.run;

import io.github.agebe.script.ScriptException;

public class DoubleValue implements Value {

  private double val;

  public DoubleValue(Object o) {
    super();
    val = ((Number)o).doubleValue();
  }

  public DoubleValue(double val) {
    super();
    this.val = val;
  }

  @Override
  public Class<?> type() {
    return double.class;
  }

  @Override
  public Object val() {
    return val;
  }

  @Override
  public boolean isNull() {
    return false;
  }

  @Override
  public boolean isBoolean() {
    return false;
  }

  @Override
  public boolean toBoolean() {
    throw new ScriptException("double can't be converted to boolean");
  }

}
