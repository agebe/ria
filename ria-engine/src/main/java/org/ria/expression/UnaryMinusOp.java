package org.ria.expression;

import org.ria.ScriptException;
import org.ria.run.ScriptContext;
import org.ria.value.ByteValue;
import org.ria.value.DoubleValue;
import org.ria.value.FloatValue;
import org.ria.value.IntValue;
import org.ria.value.LongValue;
import org.ria.value.ShortValue;
import org.ria.value.Value;

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
