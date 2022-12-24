package org.rescript.expression;

import org.rescript.ScriptException;
import org.rescript.run.ScriptContext;
import org.rescript.value.ByteValue;
import org.rescript.value.IntValue;
import org.rescript.value.LongValue;
import org.rescript.value.ShortValue;
import org.rescript.value.Value;

public class BitNotOp implements Expression {

  private Expression expression;

  public BitNotOp(Expression expression) {
    super();
    this.expression = expression;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value v = expression.eval(ctx);
    if(v.isChar()) {
      v = new IntValue(v.toInt());
    }
    if(v.isNumber()) {
      if(v.isDouble()) {
        return new LongValue(~v.toLong());
      } else if(v.isFloat()) {
        return new LongValue(~v.toLong());
      } else if(v.isLong()) {
        return new LongValue(~v.toLong());
      } else if(v.isInteger()) {
        return new IntValue(~v.toInt());
      } else if(v.isShort()) {
        return new ShortValue(~v.toShort());
      } else if(v.isByte()) {
        return new ByteValue(~v.toByte());
      } else {
        throw new ScriptException("unexpected case on bitwise complement, type '%s' not supported"
            .formatted(v.type()));
      }
    } else {
      throw new ScriptException("bitwise complement requires numbers but got '%s'"
          .formatted(v.type()));
    }
  }

}
