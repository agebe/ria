package org.ria.statement;

import org.ria.ScriptException;
import org.ria.expression.Expression;
import org.ria.run.ScriptContext;
import org.ria.util.ExceptionUtils;
import org.ria.value.Value;

public class ThrowStatement extends AbstractStatement implements Statement {

  private Expression expression;

  public ThrowStatement(int lineNumber, Expression expression) {
    super(lineNumber);
    this.expression = expression;
  }

  @Override
  public void execute(ScriptContext ctx) {
    Value v = expression.eval(ctx);
    if(v.val() instanceof Throwable t) {
      ExceptionUtils.wrapCheckedAndThrow(t);
    } else {
      throw new ScriptException("expression evaluated to '%s' but throw statement requires Throwable".formatted(v));
    }
  }

}
