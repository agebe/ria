package org.rescript.value;

import org.rescript.ScriptException;

public class ObjValue implements Value {

  public static final ObjValue NULL = new ObjValue(Object.class, null);

  private final Class<?> type;

  private final Object val;

  public ObjValue(Class<?> type, Object val) {
    super();
    this.type = type;
    this.val = val;
  }

  public Class<?> getType() {
    return type;
  }

  public Object getVal() {
    return val;
  }

  @Override
  public Class<?> type() {
    return getType();
  }

  @Override
  public Object val() {
    return getVal();
  }

  @Override
  public String toString() {
    return "ObjValue [type=" + type + "]";
  }

  @Override
  public boolean isBoolean() {
    return Boolean.class.equals(type) || boolean.class.equals(type);
  }

  @Override
  public boolean isNumber() {
    return isDouble() || isFloat() || isLong() || isInteger() || isByte() || isShort();
  }

  @Override
  public boolean isString() {
    return String.class.equals(type);
  }

  @Override
  public boolean toBoolean() {
    return (Boolean)val;
  }

  @Override
  public double toDouble() {
    return ((Number)val).doubleValue();
  }

  @Override
  public float toFloat() {
    return ((Number)val).floatValue();
  }

  @Override
  public boolean isNull() {
    return val == null;
  }

  @Override
  public int toInt() {
    return ((Number)val).intValue();
  }

  @Override
  public long toLong() {
    return ((Number)val).longValue();
  }

  @Override
  public char toChar() {
    return (Character)val;
  }

  @Override
  public byte toByte() {
    return (Byte)val;
  }

  @Override
  public short toShort() {
    return (Short)val;
  }

  @Override
  public boolean isDouble() {
    return Double.class.equals(type) || double.class.equals(type);
  }

  @Override
  public boolean isFloat() {
    return Float.class.equals(type) || float.class.equals(type);
  }

  @Override
  public boolean isLong() {
    return Long.class.equals(type) || long.class.equals(type);
  }

  @Override
  public boolean isInteger() {
    return Integer.class.equals(type) || int.class.equals(type);
  }

  @Override
  public boolean isChar() {
    return Character.class.equals(type) || char.class.equals(type);
  }

  @Override
  public boolean isByte() {
    return Byte.class.equals(type) || byte.class.equals(type);
  }

  @Override
  public boolean isShort() {
    return Short.class.equals(type) || short.class.equals(type);
  }

  @Override
  public String getText() {
    return isNotNull()?val.toString():null;
  }

  @Override
  public boolean isPrimitive() {
    return boolean.class.equals(type) ||
        double.class.equals(type) ||
        float.class.equals(type) ||
        long.class.equals(type) ||
        int.class.equals(type) ||
        char.class.equals(type) ||
        byte.class.equals(type) ||
        short.class.equals(type);
  }

  private boolean isPrimitiveWrapper() {
    return !isPrimitive() && ( isNumber() || isChar() || isBoolean() );
  }

  @Override
  public Value unbox() {
    if(isBoolean()) {
      return new BooleanValue(toBoolean());
    } else if(isDouble()) {
      return new DoubleValue(toDouble());
    } else if(isFloat()) {
      return new FloatValue(toFloat());
    } else if(isLong()) {
      return new LongValue(toLong());
    } else if(isInteger()) {
      return new IntValue(toInt());
    } else if(isChar()) {
      return new CharValue(toChar());
    } else if(isByte()) {
      return new ByteValue(toByte());
    } else if(isShort()) {
      return new ShortValue(toShort());
    } else {
      throw new ScriptException("can't unbox type '%s'".formatted(type));
    }
  }

  @Override
  public boolean equalsOp(Value other) {
    if(this.isPrimitive()) {
      return unbox().equalsOp(other);
    } else if(this.isPrimitiveWrapper() && other.isPrimitive()) {
      return unbox().equalsOp(other);
    } else if(other instanceof ObjValue) {
      return this.val == ((ObjValue)other).val;
    } else if(other instanceof EvaluatedFromValue) {
      return this.val == ((EvaluatedFromValue)other).getWrapped().val();
    } else {
      throw new ScriptException("equals op not implemented with this type '%s', other type '%s'"
          .formatted(this.type, other.type()));
    }
  }

  @Override
  public Value inc() {
    if(isNumber()) {
      // TODO if the value was an object wrapper, autobox before return
      return unbox().inc();
    } else {
      throw new ScriptException("inc requires number, " + type());
    }
  }

  @Override
  public Value dec() {
    if(isNumber()) {
      // TODO if the value was an object wrapper, autobox before return
      return unbox().dec();
    } else {
      throw new ScriptException("dec requires number, " + type());
    }
  }

}
