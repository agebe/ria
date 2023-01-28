package org.ria.symbol;

import java.lang.reflect.Field;

import org.ria.expression.CastOp;
import org.ria.parser.Type;
import org.ria.run.ScriptContext;
import org.ria.value.Value;

public class ObjectVarSymbol implements VarSymbol {

  private Object o;

  private Field f;

  private ScriptContext ctx;

  public ObjectVarSymbol(Object o, Field f, ScriptContext ctx) {
    super();
    this.o = o;
    this.f = f;
    this.ctx = ctx;
  }

  @Override
  public Value get() {
    return Value.of(f.getType(), getObjectOrNull());
  }

  @Override
  public Value inc() {
    Value v = getVal().inc();
    setVal(v);
    return getVal();
  }

  @Override
  public Value dec() {
    Value v = getVal().dec();
    setVal(v);
    return getVal();
  }

  @Override
  public String getName() {
    return f.getName();
  }

  @Override
  public Value getVal() {
    return get();
  }

  @Override
  public Object getObjectOrNull() {
    return ctx.getFirewall().checkAndGet(f, o);
  }

  @Override
  public void setVal(Value val) {
    Value v = CastOp.castTo(val, new Type(f.getType()), ctx);
    ctx.getFirewall().checkAndSet(f, o, v);
  }

  @Override
  public void set(Value v) {
    setVal(v);
  }

}
