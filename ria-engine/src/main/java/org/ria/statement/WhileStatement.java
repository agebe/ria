package org.ria.statement;

import org.ria.ScriptException;
import org.ria.expression.Expression;
import org.ria.run.ScriptContext;

public class WhileStatement extends AbstractLoop implements ContainerStatement {

  private Expression expression;

  private Statement statement;

  public WhileStatement(int lineNumber) {
    super(lineNumber);
  }

  public Expression getExpression() {
    return expression;
  }

  public void setExpression(Expression expression) {
    this.expression = expression;
  }

  @Override
  protected void executeLoop(ScriptContext ctx) {
    while(expression.eval(ctx).toBoolean()) {
      clearContinue();
      statement.execute(ctx);
      if(ctx.isReturnFlag()) {
        break;
      }
      if(isBreak()) {
        break;
      }
    }
  }

  @Override
  public void addStatement(Statement statement) {
    if(this.statement == null) {
      this.statement = statement;
    } else {
      throw new ScriptException("while already has statement '%s', can't add statement '%s'"
          .formatted(this.statement, statement));
    }
  }

}
