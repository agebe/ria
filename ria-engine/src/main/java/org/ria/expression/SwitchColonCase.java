package org.ria.expression;

import org.ria.run.ScriptContext;
import org.ria.statement.BlockStatement;
import org.ria.value.Value;

public class SwitchColonCase {

  private Expression caseExpression;

  private BlockStatement block;

  public SwitchColonCase(Expression caseExpression, BlockStatement block) {
    super();
    this.caseExpression = caseExpression;
    this.block = block;
  }

  public boolean isCase(ScriptContext ctx, Value v) {
    if(caseExpression == null) {
      // the default case
      return true;
    } else {
      Value vCase = caseExpression.eval(ctx);
      return v.equalsOp(vCase);
    }
  }

  public void execute(ScriptContext ctx) {
    block.execute(ctx);
  }

}
