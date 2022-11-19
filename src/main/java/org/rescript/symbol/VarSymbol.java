package org.rescript.symbol;

import org.rescript.value.Value;

public class VarSymbol implements Symbol {

  private String name;

  private Value val;

  public VarSymbol(String name, Value val) {
    super();
    this.name = name;
    this.val = val;
  }

  public String getName() {
    return name;
  }

  public Value getVal() {
    return val;
  }

  public Object getObjectOrNull() {
    return val!=null?val.val():null;
  }

  public void setVal(Value val) {
    this.val = val;
  }

  @Override
  public Value inc() {
    Value v = val.inc();
    this.val = v;
    return v;
  }

  @Override
  public Value dec() {
    Value v = val.dec();
    this.val = v;
    return v;
  }

  @Override
  public Value get() {
    return getVal();
  }

  @Override
  public String toString() {
    return "VarSymbol [name=" + name + ", val=" + val + "]";
  }

}
