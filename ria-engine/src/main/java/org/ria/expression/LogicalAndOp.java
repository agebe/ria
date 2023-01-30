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

import org.ria.run.ScriptContext;
import org.ria.value.BooleanValue;
import org.ria.value.Value;

public class LogicalAndOp implements Expression {

  private Expression exp1;

  private Expression exp2;

  public LogicalAndOp(Expression exp1, Expression exp2) {
    super();
    this.exp1 = exp1;
    this.exp2 = exp2;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    if(exp1.eval(ctx).toBoolean() == false) {
      return BooleanValue.FALSE;
    }
    return BooleanValue.valueOf(exp2.eval(ctx).toBoolean());
  }

  @Override
  public String getText() {
    return exp1.getText() + "&&" + exp2.getText();
  }

}
