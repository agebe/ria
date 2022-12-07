package org.rescript.statement;

import org.rescript.CheckedExceptionWrapper;
import org.rescript.ScriptException;
import org.rescript.expression.Expression;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

public class ThrowStatement implements Statement {

  private Expression expression;

  public ThrowStatement(Expression expression) {
    super();
    this.expression = expression;
  }

  @Override
  public void execute(ScriptContext ctx) {
    Value v = expression.eval(ctx);
    if(v.val() instanceof Throwable t) {
      if(v.val() instanceof RuntimeException r) {
        throw r;
      } else if(v.val() instanceof Error error) {
        throw error;
      } else {
        throw new CheckedExceptionWrapper(t);
      }
    } else {
      throw new ScriptException("expression evaluated to '%s' but throw statement requires Throwable".formatted(v));
    }
  }

}
