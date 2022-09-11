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
    throw new ScriptException("can't cast '%s' to boolean".formatted(this.getClass()));
  }

  default double toDouble() {
    throw new ScriptException("can't cast '%s' to double".formatted(this.getClass()));
  }

  default float toFloat() {
    throw new ScriptException("can't cast '%s' to float".formatted(this.getClass()));
  }

  default int toInt() {
    throw new ScriptException("can't cast '%s' to int".formatted(this.getClass()));
  }

  default long toLong() {
    throw new ScriptException("can't cast '%s' to long".formatted(this.getClass()));
  }

  static Value of(Class<?> cls, Object val) {
    if(Void.class.equals(cls) || (cls == void.class)) {
      return new VoidValue();
    } else if(cls.isPrimitive()) {
      if(boolean.class == cls) {
        return new BooleanValue(val);
      } else if(double.class == cls) {
        return new DoubleValue(val);
      } else if(float.class == cls) {
        return new FloatValue(val);
      } else if(int.class == cls) {
        return new IntValue(val);
      } else if(long.class == cls) {
        return new LongValue(val);
      } else {
        throw new ScriptException("primitive type '%s' not implemented yet".formatted(cls));
      }
    } else {
      return new ObjValue(cls, val);
    }
  }

}
