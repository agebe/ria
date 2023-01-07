package org.rescript.symbol;

import org.rescript.value.MethodValue;
import org.rescript.value.Value;

public class ObjectMethodSymbol implements VarSymbol {

  private MethodValue v;

  public ObjectMethodSymbol(MethodValue v) {
    super();
    this.v = v;
  }

  @Override
  public Value get() {
    return v;
  }

  @Override
  public Value inc() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Value dec() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Value getVal() {
    return get();
  }

  @Override
  public Object getObjectOrNull() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setVal(Value val) {
    // TODO Auto-generated method stub
    
  }

}