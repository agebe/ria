package org.rescript.parser;

import org.rescript.run.Expressions;
import org.rescript.value.Value;

public class ExpressionStatement implements Statement {

  private Expression expression;

  public ExpressionStatement(Expression expression) {
    super();
    this.expression = expression;
  }

  public Value execute(Expressions expressions) {
    return expression.eval(expressions);
  }

  @Override
  public String toString() {
    return "ExpressionStatement [expression=" + expression + "]";
  }

}
