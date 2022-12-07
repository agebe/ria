package org.rescript.statement;

import org.rescript.ScriptException;
import org.rescript.expression.Expression;
import org.rescript.run.ScriptContext;

public class DoWhileStatement extends AbstractLoop implements ContainerStatement {

  private Expression expression;

  private Statement statement;

  public DoWhileStatement(int lineNumber) {
    super(lineNumber);
  }

  public void setExpression(Expression expression) {
    this.expression = expression;
  }

  @Override
  protected void executeLoop(ScriptContext ctx) {
    do {
      clearContinue();
      statement.execute(ctx);
      if(ctx.isReturnFlag()) {
        break;
      }
      if(isBreak()) {
        break;
      }
    } while(expression.eval(ctx).toBoolean());
  }

  @Override
  public void addStatement(Statement statement) {
    if(this.statement == null) {
      this.statement = statement;
    } else {
      throw new ScriptException("do-while already has statement '%s', can't add statement '%s'"
          .formatted(this.statement, statement));
    }
  }

}
