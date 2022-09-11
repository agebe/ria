package org.rescript.run;

import org.rescript.ScriptException;

public class PackageValue implements Value {

  private String packageName;

  public PackageValue(String packageName) {
    super();
    this.packageName = packageName;
  }

  public String getPackageName() {
    return packageName;
  }

  @Override
  public Class<?> type() {
    throw new ScriptException();
  }

  @Override
  public Object val() {
    throw new ScriptException();
  }

  @Override
  public double toDouble() {
    throw new ScriptException();
  }

  @Override
  public float toFloat() {
    throw new ScriptException();
  }

  @Override
  public int toInt() {
    throw new ScriptException();
  }

  @Override
  public long toLong() {
    throw new ScriptException();
  }

  @Override
  public String toString() {
    return "PackageValue [packageName=" + packageName + "]";
  }

}
