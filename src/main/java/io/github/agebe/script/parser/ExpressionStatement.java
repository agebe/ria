package io.github.agebe.script.parser;

import io.github.agebe.script.run.Expressions;
import io.github.agebe.script.run.Value;

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
