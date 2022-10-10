package org.rescript.statement;

import java.util.List;

import org.rescript.expression.Expression;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

public class ForStatement implements Statement {

  private ForInitStatement forInit;

  private Expression forTerm;

  private List<Expression> forInc;

  private Statement statement;

  public ForStatement(ForInitStatement forInit, Expression forTerm, List<Expression> forInc, Statement statement) {
    super();
    this.forInit = forInit;
    this.forTerm = forTerm;
    this.forInc = forInc;
    this.statement = statement;
  }

  @Override
  public void execute(ScriptContext ctx) {
    if(forInit != null) {
      forInit.execute(ctx);
    }
    for(;;) {
      if(forTerm!=null) {
        Value bool = forTerm.eval(ctx);
        if(!bool.toBoolean()) {
          break;
        }
      }
      statement.execute(ctx);
      if(ctx.isReturnFlag()) {
        break;
      }
      if(forInc != null) {
        forInc.forEach(inc -> inc.eval(ctx));
      }
    }
  }

}
