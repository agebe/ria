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

import org.ria.expression.Expression;
import org.ria.run.ScriptContext;
import org.ria.value.Value;
import org.ria.value.VoidValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReturnStatement extends AbstractStatement implements Statement {

  private static final Logger log = LoggerFactory.getLogger(ReturnStatement.class);

  private Expression expression;

  public ReturnStatement(int lineNumber, Expression expression) {
    super(lineNumber);
    this.expression = expression;
  }

  @Override
  public void execute(ScriptContext ctx) {
    log.debug("execute return statement, expression " + expression);
    if(expression != null) {
      Value v = expression.eval(ctx);
      ctx.setLastResult(v);
    } else {
      ctx.setLastResult(VoidValue.VOID);
    }
    ctx.setReturnFlag(true);
  }

  @Override
  public String toString() {
    return "ReturnStatement [expression=" + expression + "]";
  }

}
