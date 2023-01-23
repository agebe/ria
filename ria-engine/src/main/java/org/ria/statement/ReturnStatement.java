package org.ria.statement;

import org.ria.expression.Expression;
import org.ria.run.ScriptContext;
import org.ria.value.Value;
import org.ria.value.VoidValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReturnStatement extends AbstractStatement implements Statement {

  private static final Logger log = LoggerFactory.getLogger(ReturnStatement.class);

  private Expression expression;

  public ReturnStatement(int lineNumber, Expression expression) {
    super(lineNumber);
    this.expression = expression;
  }

  @Override
  public void execute(ScriptContext ctx) {
    log.debug("execute return statement, expression " + expression);
    if(expression != null) {
      Value v = expression.eval(ctx);
      ctx.setLastResult(v);
    } else {
      ctx.setLastResult(VoidValue.VOID);
    }
    ctx.setReturnFlag(true);
  }

  @Override
  public String toString() {
    return "ReturnStatement [expression=" + expression + "]";
  }

}