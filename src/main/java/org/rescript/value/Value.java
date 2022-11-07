package org.rescript.value;

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

  default boolean isNumber() {
    return false;
  }

  default boolean isDouble() {
    return false;
  }

  default boolean isFloat() {
    return false;
  }

  default boolean isLong() {
    return false;
  }

  default boolean isInteger() {
    return false;
  }

  default boolean isChar() {
    return false;
  }

  default boolean isString() {
    return false;
  }

  default boolean isFunction() {
    return false;
  }

  default boolean isMethod() {
    return false;
  }

  boolean isPrimitive();

  boolean equalsOp(Value other);

  default String getText() {
    throw new ScriptException("no text representation implemented");
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

  default char toChar() {
    throw new ScriptException("can't cast '%s' to char".formatted(this.getClass()));
  }

  default FunctionValue toFunctionValue() {
    throw new ScriptException("can't cast '%s' to function value".formatted(this.getClass()));
  }

  default MethodValue toMethodValue() {
    throw new ScriptException("can't cast '%s' to method value".formatted(this.getClass()));
  }

  default Value unbox() {
    return this;
  }

  default Value inc() {
    throw new ScriptException("increment not supported");
  }

  default Value dec() {
    throw new ScriptException("decrement not supported");
  }

  default String typeOf() {
    return type().getName();
  }

  static Value of(Class<?> cls, Object val) {
    if(Void.class.equals(cls) || (cls == void.class)) {
      return VoidValue.VOID;
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
      } else if(char.class == cls) {
        return new CharValue(val);
      } else {
        throw new ScriptException("primitive type '%s' not implemented yet".formatted(cls));
      }
    } else if(cls.isArray()) {
      if(cls == boolean[].class) {
        return new BooleanArrayValue((boolean[])val);
      } else if(cls == double[].class) {
        return new DoubleArrayValue((double[])val);
      } else if(cls == float[].class) {
        return new FloatArrayValue((float[])val);
      } else if(cls == int[].class) {
        return new IntArrayValue((int[])val);
      } else if(cls == long[].class) {
        return new LongArrayValue((long[])val);
      } else if(cls == char[].class) {
        return new CharArrayValue((char[])val);
      } else if(cls == short[].class) {
        throw new ScriptException("short array not supported");
      } else if(cls == byte[].class) {
        throw new ScriptException("byte array not supported");
      } else {
        return new ArrayValue((Object[])val, cls);
      }
    } else {
      return new ObjValue(cls, val);
    }
  }

  static Value of(Object val) {
    if(val != null) {
      return of(val.getClass(), val);
    } else {
      return of(Object.class, null);
    }
  }

}
