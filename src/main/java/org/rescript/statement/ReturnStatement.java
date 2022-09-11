package org.rescript.statement;

import org.rescript.parser.Expression;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;
import org.rescript.value.VoidValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReturnStatement implements Statement {

  private static final Logger log = LoggerFactory.getLogger(ReturnStatement.class);

  private Expression expression;

  public ReturnStatement(Expression expression) {
    super();
    this.expression = expression;
  }

  @Override
  public void execute(ScriptContext ctx) {
    log.debug("execute return statement, expression " + expression);
    if(expression != null) {
      Value v = expression.eval(ctx.getExpressions());
      ctx.setLastResult(v);
    } else {
      ctx.setLastResult(new VoidValue());
    }
    ctx.setCurrent(null);
  }

}
