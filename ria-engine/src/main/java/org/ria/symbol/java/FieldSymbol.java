package org.ria.symbol.java;

import java.lang.reflect.Field;

import org.ria.ScriptException;
import org.ria.symbol.Symbol;
import org.ria.value.Value;

public class FieldSymbol implements Symbol {

  private Field field;

  private Object owner;

  public FieldSymbol(Field field, Object owner) {
    super();
    this.field = field;
    this.owner = owner;
  }

  @Override
  public Value get() {
    try {
      return Value.of(field.getType(), field.get(owner));
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new ScriptException("failed on field '%s' get".formatted(field), e);
    }
  }

  @Override
  public Value inc() {
    Value v = get().inc();
    set(v);
    return v;
  }

  @Override
  public Value dec() {
    Value v = get().dec();
    set(v);
    return v;
  }

  private void set(Value v) {
    try {
      if(v.isPrimitive()) {
        if(v.isDouble()) {
          field.setDouble(owner, v.toDouble());
        } else if(v.isFloat()) {
          field.setFloat(owner, v.toFloat());
        } else if(v.isLong()) {
          field.setLong(owner, v.toLong());
        } else if(v.isInteger()) {
          field.setInt(owner, v.toInt());
        } else {
          throw new ScriptException("unsupported primitive type, " + v.type());
        }
      } else {
        field.set(owner, v.val());
      }
    } catch(Exception e) {
      throw new ScriptException("failed on field '%s' set".formatted(field), e);
    }
  }

}
