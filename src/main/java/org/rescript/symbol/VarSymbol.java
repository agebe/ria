package org.rescript.symbol;

import org.rescript.expression.CastOp;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

public class VarSymbol implements Symbol {

  private String name;

  private Value val;

  private String type;

  private ScriptContext ctx;

  public VarSymbol(String name, Value val, String type, ScriptContext ctx) {
    super();
    this.name = name;
    this.val = val;
    this.type = type;
    this.ctx = ctx;
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
    if(type != null) {
      this.val = new CastOp(type, c -> val).eval(ctx);
    } else {
      this.val = val;
    }
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
