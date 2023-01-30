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
import org.ria.expression.Expression;
import org.ria.run.ScriptContext;

public class DoWhileStatement extends AbstractLoop implements ContainerStatement {

  private Expression expression;

  private Statement statement;

  public DoWhileStatement(int lineNumber) {
    super(lineNumber);
  }

  public void setExpression(Expression expression) {
    this.expression = expression;
  }

  @Override
  protected void executeLoop(ScriptContext ctx) {
    do {
      clearContinue();
      statement.execute(ctx);
      if(ctx.isReturnFlag()) {
        break;
      }
      if(isBreak()) {
        break;
      }
    } while(expression.eval(ctx).toBoolean());
  }

  @Override
  public void addStatement(Statement statement) {
    if(this.statement == null) {
      this.statement = statement;
    } else {
      throw new ScriptException("do-while already has statement '%s', can't add statement '%s'"
          .formatted(this.statement, statement));
    }
  }

}
