package org.ria.statement;

import org.ria.expression.Expression;
import org.ria.run.ScriptContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpressionStatement extends AbstractStatement implements Statement {

  private static final Logger log = LoggerFactory.getLogger(ExpressionStatement.class);

  private Expression expression;

  public ExpressionStatement(int lineNumber, Expression expression) {
    super(lineNumber);
    this.expression = expression;
  }

  @Override
  public void execute(ScriptContext ctx) {
    log.debug("execute expression '{}'", expression);
    ctx.setLastResult(expression.eval(ctx));
  }

  @Override
  public String toString() {
    return "ExpressionStatement [expression=" + expression + "]";
  }

}
