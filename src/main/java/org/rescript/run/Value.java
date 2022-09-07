package org.rescript.run;

import org.rescript.ScriptException;

public interface Value {

  Class<?> type();

  Object val();

  default boolean isNull() {
    return false;
  }

  default boolean isNotNull() {
    return !isNull();
  }

  default boolean isBoolean() {
    return false;
  }

  default boolean toBoolean() {
    throw new ScriptException("'%s' can't be cast to boolean".formatted(type()));
  }

  double toDouble();

  float toFloat();

  int toInt();

  long toLong();

  static Value of(Class<?> cls, Object val) {
    if(Void.class.equals(cls) || (cls == void.class)) {
      return new VoidValue();
    } else if(cls.isPrimitive()) {
      if(boolean.class == cls) {
        return new BooleanValue(val);
      } else if(double.class == cls) {
        return new DoubleValue(val);
      } else if(int.class == cls) {
        return new IntValue(val);
      } else if(int.class == cls) {
        return new LongValue(val);
      } else {
        throw new ScriptException("primitive type '%s' not implemented yet".formatted(cls));
      }
    } else {
      return new ObjValue(cls, val);
    }
  }

}
