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
package org.ria.expression;

import org.ria.ScriptException;
import org.ria.run.ScriptContext;
import org.ria.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DotOperator implements Expression {

  private static final Logger log = LoggerFactory.getLogger(DotOperator.class);

  private Expression expr1;

  private TargetExpression expr2;

  public DotOperator(Expression expr1, TargetExpression expr2) {
    super();
    this.expr1 = expr1;
    this.expr2 = expr2;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value val = expr1.eval(ctx);
    log.debug("eval expr1 '{}' to '{}', isNull '{}'", expr1, val, val!=null?val.isNull():true);
    if(val != null) {
      return expr2.eval(ctx, val);
    } else {
      throw new ScriptException("left expression '%s' evaluated to null".formatted(expr1));
    }
  }

  @Override
  public String getText() {
    return expr1 + "." + expr2;
  }

  @Override
  public String toString() {
    return getText();
  }

}
