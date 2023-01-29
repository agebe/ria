package org.ria.symbol;

import org.ria.ScriptException;
import org.ria.expression.CastOp;
import org.ria.parser.Type;
import org.ria.run.ScriptContext;
import org.ria.value.Value;

public class ScriptVarSymbol implements VarSymbol {

  private String name;

  private Value val;

  private Type type;

  private boolean initialized;

  private ScriptContext ctx;

  public ScriptVarSymbol(String name, Value val, Type type, boolean initialized, ScriptContext ctx) {
    super();
    this.name = name;
    this.val = val;
    this.type = type;
    this.initialized = initialized;
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

  private Value setValue(Value val) {
    if(type != null) {
      if(initialized && type.isVal()) {
        throw new ScriptException("val variable '%s' can not change".formatted(name));
      } else {
        this.val = CastOp.castTo(val, type, ctx);
      }
    } else {
      this.val = val;
    }
    initialized = true;
    return this.val;
  }

  @Override
  public void setVal(Value val) {
    setValue(val);
  }

  @Override
  public Value inc() {
    return setValue(val.inc());
//    Value v = val.inc();
//    this.val = v;
//    return v;
  }

  @Override
  public Value dec() {
    return setValue(val.dec());
//    Value v = val.dec();
//    this.val = v;
//    return v;
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
    setValue(v);
  }

}
