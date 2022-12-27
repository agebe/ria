package org.rescript.expression;

import org.rescript.parser.ParseItem;
import org.rescript.run.ScriptContext;
import org.rescript.statement.BlockStatement;
import org.rescript.value.Value;

public class SwitchColonCase implements ParseItem {

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
