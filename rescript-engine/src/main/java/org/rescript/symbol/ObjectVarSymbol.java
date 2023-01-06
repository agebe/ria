package org.rescript.symbol;

import java.lang.reflect.Field;

import org.rescript.ScriptException;
import org.rescript.expression.CastOp;
import org.rescript.parser.Type;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

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
    return v;
  }

  @Override
  public Value dec() {
    Value v = getVal().dec();
    setVal(v);
    return v;
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
    try {
      return f.get(o);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new ScriptException("failed to access field '%s'".formatted(f.getName()), e);
    }
  }

  @Override
  public void setVal(Value val) {
    Value v = CastOp.castTo(val, new Type(f.getType()), ctx);
    try {
      f.set(o, v.val());
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new ScriptException("failed to set field '%s'".formatted(f.getName()), e);
    }
  }

}
