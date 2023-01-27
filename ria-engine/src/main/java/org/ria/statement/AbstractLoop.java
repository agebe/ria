package org.ria.statement;

import org.ria.ScriptException;
import org.ria.run.ScriptContext;

public abstract class AbstractLoop extends AbstractStatement implements Breakable, Continueable, Statement {

  private boolean breakFlag;

  private boolean continueFlag;

  public AbstractLoop(int lineNumber) {
    super(lineNumber);
  }

  @Override
  public void setContinue() {
    continueFlag = true;
  }

  @Override
  public boolean isContinue() {
    return continueFlag;
  }

  @Override
  public void setBreak() {
    breakFlag = true;
  }

  @Override
  public boolean isBreak() {
    return breakFlag;
  }

  protected void clearBreak() {
    breakFlag = false;
  }

  protected void clearContinue() {
    continueFlag = false;
  }

  @Override
  public final void execute(ScriptContext ctx) {
    if(!ctx.getFeatures().isLoopsEnabled()) {
      throw new ScriptException("loops are disabled (via script features)");
    }
    try {
      clearBreak();
      clearContinue();
      ctx.getCurrentFrame().pushBreakable(this);
      ctx.getCurrentFrame().pushContinueable(this);
      executeLoop(ctx);
    } finally {
      ctx.getCurrentFrame().popContinueable(this);
      ctx.getCurrentFrame().popBreakable(this);
    }
  }

  protected abstract void executeLoop(ScriptContext ctx);
}
