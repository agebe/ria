package org.rescript.statement;

import java.util.List;

import org.rescript.expression.Expression;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

public class ForStatement extends AbstractLoop {

  private ForInitStatement forInit;

  private Expression forTerm;

  private List<Expression> forInc;

  private Statement statement;

  public ForStatement(
      int lineNumber,
      ForInitStatement forInit,
      Expression forTerm,
      List<Expression> forInc,
      Statement statement) {
    super(lineNumber);
    this.forInit = forInit;
    this.forTerm = forTerm;
    this.forInc = forInc;
    this.statement = statement;
  }

  @Override
  protected void executeLoop(ScriptContext ctx) {
    try {
      ctx.getSymbols().getScriptSymbols().enterScope();
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
        clearContinue();
        statement.execute(ctx);
        if(ctx.isReturnFlag()) {
          break;
        }
        if(isBreak()) {
          break;
        }
        // nothing to do here for continue, we just have to make sure to break out of the block! (see BlockStatement)
        if(forInc != null) {
          forInc.forEach(inc -> inc.eval(ctx));
        }
      }
    } finally {
      ctx.getSymbols().getScriptSymbols().exitScope();
    }
  }

}
