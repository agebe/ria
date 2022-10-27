package org.rescript.expression;

import org.rescript.ScriptException;
import org.rescript.run.ScriptContext;
import org.rescript.value.BooleanValue;
import org.rescript.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstanceOfOp implements Expression {

  private static final Logger log = LoggerFactory.getLogger(InstanceOfOp.class);

  private Expression expr1;

  private Expression type;

  private Identifier bind;

  public InstanceOfOp(Expression expr1, Expression type, Identifier bind) {
    super();
    this.expr1 = expr1;
    this.type = type;
    this.bind = bind;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value v1 = expr1.eval(ctx);
    if(v1 == null) {
      throw new ScriptException("left operand did not resolve '%s'".formatted(expr1.getText()));
    }
    Value v2 = type.eval(ctx);
    if(v2 == null ) {
      throw new ScriptException("right operand did not resolve '%s'".formatted(type.getText()));
    }
    log.trace("v1 '{}', v1 val '{}', v2 '{}', v2.val '{}'", v1, v1.val(), v2, v2.val());
    //https://stackoverflow.com/questions/496928/what-is-the-difference-between-instanceof-and-class-isassignablefrom
    boolean isInstanceOf = v2.type().isAssignableFrom(v1.type());
    if(isInstanceOf && (bind != null)) {
      ctx.getSymbols().getScriptSymbols().defineVar(bind.getIdent(), v1);
    }
    return BooleanValue.of(isInstanceOf);
  }

}
