package org.ria.symbol;

import org.ria.expression.CastOp;
import org.ria.parser.Type;
import org.ria.run.ScriptContext;
import org.ria.value.Value;

public class ScriptVarSymbol implements VarSymbol {

  private String name;

  private Value val;

  private Type type;

  private ScriptContext ctx;

  public ScriptVarSymbol(String name, Value val, Type type, ScriptContext ctx) {
    super();
    this.name = name;
    this.val = val;
    this.type = type;
    this.ctx = ctx;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Value getVal() {
    return val;
  }

  @Override
  public Object getObjectOrNull() {
    return val!=null?val.val():null;
  }

  @Override
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

  @Override
  public void set(Value v) {
    this.val = v;
  }

}
