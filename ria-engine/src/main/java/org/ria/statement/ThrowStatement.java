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
import org.ria.util.ExceptionUtils;
import org.ria.value.Value;

public class ThrowStatement extends AbstractStatement implements Statement {

  private Expression expression;

  public ThrowStatement(int lineNumber, Expression expression) {
    super(lineNumber);
    this.expression = expression;
  }

  @Override
  public void execute(ScriptContext ctx) {
    Value v = expression.eval(ctx);
    if(v.val() instanceof Throwable t) {
      ExceptionUtils.wrapCheckedAndThrow(t);
    } else {
      throw new ScriptException("expression evaluated to '%s' but throw statement requires Throwable".formatted(v));
    }
  }

}
