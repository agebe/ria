package org.ria.symbol;

import org.ria.value.Value;

public interface VarSymbol extends Symbol {

  String getName();

  Value getVal();

  Object getObjectOrNull();

  void setVal(Value val);

}
