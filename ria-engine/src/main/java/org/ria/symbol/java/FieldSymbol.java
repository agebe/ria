package org.ria.symbol.java;

import java.lang.reflect.Field;

import org.ria.run.ScriptContext;
import org.ria.symbol.Symbol;
import org.ria.value.Value;

public class FieldSymbol implements Symbol {

  private Field field;

  private Object owner;

  private ScriptContext ctx;

  public FieldSymbol(Field field, Object owner, ScriptContext ctx) {
    super();
    this.field = field;
    this.owner = owner;
    this.ctx = ctx;
  }

  @Override
  public Value get() {
    return Value.of(field.getType(), ctx.getFirewall().checkAndGet(field, owner));
  }

  @Override
  public Value inc() {
    Value v = get().inc();
    set(v);
    return get();
  }

  @Override
  public Value dec() {
    Value v = get().dec();
    set(v);
    return get();
  }

  @Override
  public void set(Value v) {
    ctx.getFirewall().checkAndSet(field, owner, v);
  }

}
