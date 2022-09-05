package io.github.agebe.script.run;

import io.github.agebe.script.ScriptException;

public interface Value {

  Class<?> type();

  Object val();

  boolean isNull();

  default boolean isNotNull() {
    return !isNull();
  }

  boolean isBoolean();

  boolean toBoolean();

  static Value of(Class<?> cls, Object val) {
    if(cls.equals(Void.class) || (cls == void.class)) {
      return new VoidValue();
    } else if(cls.isPrimitive()) {
      if(boolean.class == cls) {
        return new BooleanValue(val);
      } else if(double.class == cls) {
        return new DoubleValue(val);
      } else {
        throw new ScriptException("primitive type '%s' not implemented yet".formatted(cls));
      }
    } else {
      return new ObjValue(cls, val);
    }
  }

}
