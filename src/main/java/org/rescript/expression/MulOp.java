package org.rescript.expression;

import org.rescript.ScriptException;
import org.rescript.run.ScriptContext;
import org.rescript.value.DoubleValue;
import org.rescript.value.FloatValue;
import org.rescript.value.IntValue;
import org.rescript.value.LongValue;
import org.rescript.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MulOp extends TripleOp {

  private static final Logger log = LoggerFactory.getLogger(MulOp.class);

  public MulOp(Expression exp1, Expression exp2) {
    super(exp1, exp2, "*");
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value v1 = getExp1().eval(ctx);
    Value v2 = getExp2().eval(ctx);
    if(v1.isChar() && !v2.isString()) {
      v1 = new IntValue(v1.toInt());
    }
    if(v2.isChar() && !v1.isString()) {
      v2 = new IntValue(v2.toInt());
    }
    log.debug("eval '{}' * '{}'", v1, v2);
    if(v1.isNumber() && v2.isNumber()) {
      if(v1.isDouble() || v2.isDouble()) {
        return new DoubleValue(v1.toDouble() * v2.toDouble());
      } else if(v1.isFloat() || v2.isFloat()) {
        return new FloatValue(v1.toFloat() * v2.toFloat());
      } else if(v1.isLong() || v2.isLong()) {
        return new LongValue(v1.toLong() * v2.toLong());
      } else if(v1.isInteger() || v2.isInteger()) {
        return new IntValue(v1.toInt() * v2.toInt());
      } else 
        throw new ScriptException("unexpected case on '%s', types '%s' and '%s'"
            .formatted(getOp(), v1.type(), v2.type()));
    } else {
      throw new ScriptException("operation '%s' requires numbers but got '%s' and '%s'"
          .formatted(getOp(), v1.type(), v2.type()));
    }
  }

}

