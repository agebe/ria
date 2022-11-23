package org.rescript.expression;

import org.rescript.ScriptException;
import org.rescript.run.ScriptContext;
import org.rescript.value.ByteValue;
import org.rescript.value.DoubleValue;
import org.rescript.value.FloatValue;
import org.rescript.value.IntValue;
import org.rescript.value.LongValue;
import org.rescript.value.ShortValue;
import org.rescript.value.Value;

public class UnaryMinusOp implements Expression {

  private Expression expr;

  public UnaryMinusOp(Expression expr) {
    super();
    this.expr = expr;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value val = expr.eval(ctx);
    if(val.isChar()) {
      val = new IntValue(val.toInt());
    }
    if(val.isNumber()) {
      if(val.isDouble()) {
        return new DoubleValue(-val.toDouble());
      } else if(val.isFloat()) {
        return new FloatValue(-val.toFloat());
      } else if(val.isLong()) {
        return new LongValue(-val.toLong());
      } else if(val.isInteger()) {
        return new IntValue(-val.toInt());
      } else if(val.isShort()) {
        return new ShortValue(-val.toShort());
      } else if(val.isByte()) {
        return new ByteValue(-val.toByte());
      } else {
        throw new ScriptException("unsupported number type: " + val.type());
      }
    } else {
      throw new ScriptException("unary minus requires number, " + val);
    }
  }

  @Override
  public String getText() {
    return "-" + expr.getText();
  }

  @Override
  public String toString() {
    return getText();
  }

}
