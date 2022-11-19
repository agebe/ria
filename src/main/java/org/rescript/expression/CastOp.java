package org.rescript.expression;

import org.rescript.ScriptException;
import org.rescript.run.ScriptContext;
import org.rescript.symbol.SymbolNotFoundException;
import org.rescript.value.BooleanValue;
import org.rescript.value.CharValue;
import org.rescript.value.DoubleValue;
import org.rescript.value.FloatValue;
import org.rescript.value.IntValue;
import org.rescript.value.LongValue;
import org.rescript.value.ObjValue;
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
    Value v = expression.eval(ctx);
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
      throw new ScriptException("cast to byte not implemented");
    } else if(type.equals("short")) {
      throw new ScriptException("cast to short not implemented");
    } else if(type.equals("boolean")) {
      return new BooleanValue(v.toBoolean());
    } else {
      Class<?> cls = ctx.getSymbols().getJavaSymbols().resolveType(type);
      if(cls == null) {
        throw new SymbolNotFoundException("type '%s' could not be resolved".formatted(type));
      }
      return new ObjValue(cls, cls.cast(v.val()));
    }
  }
}
