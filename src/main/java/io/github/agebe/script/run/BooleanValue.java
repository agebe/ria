package io.github.agebe.script.run;

public class BooleanValue implements Value {

  private boolean val;

  public BooleanValue(Object o) {
    super();
    val = (Boolean)o;
  }

  public BooleanValue(boolean val) {
    super();
    this.val = val;
  }

  @Override
  public Class<?> type() {
    return boolean.class;
  }

  @Override
  public Object val() {
    return val;
  }

  @Override
  public String toString() {
    return "BooleanValue [val=" + val + "]";
  }

  @Override
  public boolean isNull() {
    return false;
  }

  @Override
  public boolean isBoolean() {
    return true;
  }

  @Override
  public boolean toBoolean() {
    return val;
  }

}
