package org.rescript.run;

import org.rescript.ScriptException;

public class FloatValue implements Value {

  private float val;

  public FloatValue(Object o) {
    super();
    val = ((Number)o).floatValue();
  }

  public FloatValue(float val) {
    super();
    this.val = val;
  }

  @Override
  public Class<?> type() {
    return float.class;
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
  public boolean toBoolean() {
    throw new ScriptException("float can't be cast to boolean");
  }

  @Override
  public double toDouble() {
    return val;
  }

  @Override
  public float toFloat() {
    return val;
  }

}
