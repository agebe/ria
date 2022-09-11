package org.rescript.statement;

import org.rescript.parser.Expression;
import org.rescript.run.Expressions;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

public class ExpressionStatement implements Statement {

  private Expression expression;

  public ExpressionStatement(Expression expression) {
    super();
    this.expression = expression;
  }

  @Override
  public void execute(ScriptContext ctx) {
    ctx.setLastResult(execute(ctx.getExpressions()));
    ctx.setCurrent(ctx.getCurrent().getTrueNode());
  }

  private Value execute(Expressions expressions) {
    return expression.eval(expressions);
  }

  @Override
  public String toString() {
    return "ExpressionStatement [expression=" + expression + "]";
  }

}
