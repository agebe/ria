package org.rescript.expression;

import org.rescript.run.ScriptContext;
import org.rescript.symbol.SymbolNotFoundException;
import org.rescript.value.BooleanValue;
import org.rescript.value.ByteValue;
import org.rescript.value.CharValue;
import org.rescript.value.DoubleValue;
import org.rescript.value.FloatValue;
import org.rescript.value.IntValue;
import org.rescript.value.LongValue;
import org.rescript.value.ObjValue;
import org.rescript.value.ShortValue;
import org.rescript.value.Value;

public class CastOp implements Expression {

  private String type;

  private Expression expression;

  public CastOp(String type, Expression expression) {
    super();
    this.type = type;
    this.expression = expression;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    return castTo(expression.eval(ctx), type, ctx);
  }

  public static Value castTo(Value v, String type, ScriptContext ctx) {
    // TODO check if value is already of correct type and return v without creating a new value
    if(type.equals("double")) {
      return new DoubleValue(v.toDouble());
    } else if(type.equals("float")) {
      return new FloatValue(v.toFloat());
    } else if(type.equals("long")) {
      return new LongValue(v.toLong());
    } else if(type.equals("int")) {
      return new IntValue(v.toInt());
    } else if(type.equals("char")) {
      return new CharValue(v.toChar());
    } else if(type.equals("byte")) {
      return new ByteValue(v.toByte());
    } else if(type.equals("short")) {
      return new ShortValue(v.toShort());
    } else if(type.equals("boolean")) {
      return new BooleanValue(v.toBoolean());
    } else {
      Class<?> cls = ctx.getSymbols().getJavaSymbols().resolveType(type);
      if(cls == null) {
        throw new SymbolNotFoundException("type '%s' could not be resolved".formatted(type));
      }
      if(cls == Double.class) {
        return new ObjValue(cls, v.isNotNull()?v.toDouble():null);
      } else if(cls == Float.class) {
        return new ObjValue(cls, v.isNotNull()?v.toFloat():null);
      } else if(cls == Long.class) {
        return new ObjValue(cls, v.isNotNull()?v.toLong():null);
      } if(cls == Integer.class) {
        return new ObjValue(cls, v.isNotNull()?v.toInt():null);
      } if(cls == Character.class) {
        return new ObjValue(cls, v.isNotNull()?v.toChar():null);
      } if(cls == Byte.class) {
        return new ObjValue(cls, v.isNotNull()?v.toByte():null);
      } if(cls == Short.class) {
        return new ObjValue(cls, v.isNotNull()?v.toShort():null);
      } if(cls == Boolean.class) {
        return new ObjValue(cls, v.isNotNull()?v.toBoolean():null);
      } else {
        return new ObjValue(cls, cls.cast(v.val()));
      }
    }
  }

  public static boolean canCast(Value v, String type, ScriptContext ctx) {
    try {
      castTo(v, type, ctx);
      return true;
    } catch(Exception e) {
      return false;
    }
  }
}
