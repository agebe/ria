package org.rescript.expression;

import org.apache.commons.lang3.StringUtils;
import org.rescript.ScriptException;
import org.rescript.run.ScriptContext;
import org.rescript.value.BooleanValue;
import org.rescript.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EqualityOp extends TripleOp {

  private static final Logger log = LoggerFactory.getLogger(EqualityOp.class);

  public EqualityOp(Expression exp1, Expression exp2, String op) {
    super(exp1, exp2, op);
    if(!StringUtils.equalsAny(op, "==", "!=")) {
      throw new ScriptException("operation must be either == or !=");
    }
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value v1 = getExp1().eval(ctx);
    Value v2 = getExp2().eval(ctx);
    log.debug("eval '{}' == '{}'", v1, v2);
    boolean eq = v1.equalsOp(v2);
    return BooleanValue.of(getOp().equals("==")?eq:!eq);
  }

}
