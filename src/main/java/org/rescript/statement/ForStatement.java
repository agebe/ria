package org.rescript.statement;

import java.util.List;

import org.rescript.expression.Expression;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

public class ForStatement implements Statement, Breakable, Continueable {

  private ForInitStatement forInit;

  private Expression forTerm;

  private List<Expression> forInc;

  private Statement statement;

  private boolean breakFlag;

  private boolean continueFlag;

  public ForStatement(ForInitStatement forInit, Expression forTerm, List<Expression> forInc, Statement statement) {
    super();
    this.forInit = forInit;
    this.forTerm = forTerm;
    this.forInc = forInc;
    this.statement = statement;
  }

  @Override
  public void execute(ScriptContext ctx) {
    try {
      breakFlag = false;
      continueFlag = false;
      ctx.getSymbols().getScriptSymbols().enterScope();
      ctx.getCurrentFrame().pushBreakable(this);
      ctx.getCurrentFrame().pushContinueable(this);
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
        continueFlag = false;
        statement.execute(ctx);
        if(ctx.isReturnFlag()) {
          break;
        }
        if(breakFlag) {
          break;
        }
        if(forInc != null) {
          forInc.forEach(inc -> inc.eval(ctx));
        }
      }
    } finally {
      ctx.getCurrentFrame().popContinueable(this);
      ctx.getCurrentFrame().popBreakable(this);
      ctx.getSymbols().getScriptSymbols().exitScope();
    }
  }

  @Override
  public void executeBreak() {
    breakFlag = true;
  }

  @Override
  public boolean isBreak() {
    return breakFlag;
  }

  @Override
  public void executeContinue() {
    continueFlag = true;
  }

  @Override
  public boolean isContinue() {
    return continueFlag;
  }

}
