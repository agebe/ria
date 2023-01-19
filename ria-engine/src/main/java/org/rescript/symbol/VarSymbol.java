package org.rescript.symbol;

import org.rescript.value.Value;

public interface VarSymbol extends Symbol {

  String getName();

  Value getVal();

  Object getObjectOrNull();

  void setVal(Value val);

}
