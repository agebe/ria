package org.rescript.statement;

import org.rescript.expression.Expression;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;
import org.rescript.value.VoidValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReturnStatement extends AbstractStatement {

  private static final Logger log = LoggerFactory.getLogger(ReturnStatement.class);

  private Expression expression;

  public ReturnStatement(Expression expression) {
    super();
    this.expression = expression;
  }

  @Override
  public void execute(ScriptContext ctx) {
    log.debug("execute return statement, expression " + expression);
    ctx.setReturnFlag(true);
    if(expression != null) {
      Value v = expression.eval(ctx);
      ctx.setLastResult(v);
    } else {
      ctx.setLastResult(new VoidValue());
    }
    ctx.setCurrent(null);
  }

  @Override
  public String toString() {
    return "ReturnStatement [expression=" + expression + "]";
  }

}
