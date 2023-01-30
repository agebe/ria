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

import java.util.List;

import org.ria.expression.Expression;
import org.ria.run.ScriptContext;
import org.ria.value.Value;

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
