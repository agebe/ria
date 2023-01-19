package org.ria.expression;

import java.util.List;

import org.ria.run.ScriptContext;
import org.ria.statement.BlockStatement;
import org.ria.value.Value;

public class SwitchArrowCase {

  private List<Expression> caseExpressions;

  private BlockStatement block;

  public SwitchArrowCase(List<Expression> caseExpressions, BlockStatement block) {
    super();
    this.caseExpressions = caseExpressions;
    this.block = block;
  }

  public boolean isCase(ScriptContext ctx, Value v) {
    if(caseExpressions == null) {
      // the default case
      return true;
    } else {
      return caseExpressions.stream()
          .map(e -> e.eval(ctx))
          .anyMatch(vCase -> v.equalsOp(vCase));
    }
  }

  public void execute(ScriptContext ctx) {
    block.execute(ctx);
  }

}
