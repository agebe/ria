package org.rescript.statement;

import org.rescript.ScriptException;
import org.rescript.expression.Expression;
import org.rescript.run.ScriptContext;

public class WhileStatement extends AbstractStatement {

  private Expression expression;

  private Statement statement;

  public WhileStatement() {
    super();
  }

  public Expression getExpression() {
    return expression;
  }

  public void setExpression(Expression expression) {
    this.expression = expression;
  }

  @Override
  public void execute(ScriptContext ctx) {
    while(expression.eval(ctx).toBoolean()) {
      statement.execute(ctx);
      if(ctx.isReturnFlag()) {
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
