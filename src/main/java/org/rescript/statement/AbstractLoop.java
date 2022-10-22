package org.rescript.statement;

import org.rescript.run.ScriptContext;

public abstract class AbstractLoop implements Breakable, Continueable, Statement {

  private boolean breakFlag;

  private boolean continueFlag;

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
