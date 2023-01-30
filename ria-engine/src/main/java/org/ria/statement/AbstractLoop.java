/*
 * Copyright 2023 Andre Gebers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
