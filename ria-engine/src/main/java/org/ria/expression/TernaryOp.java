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
import org.ria.value.Value;

public class TernaryOp implements Expression {

  private Expression predicate;

  private Expression trueExpression;

  private Expression falseExpression;

  public TernaryOp(Expression predicate, Expression trueExpression, Expression falseExpression) {
    super();
    this.predicate = predicate;
    this.trueExpression = trueExpression;
    this.falseExpression = falseExpression;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    return predicate.eval(ctx).toBoolean()?trueExpression.eval(ctx):falseExpression.eval(ctx);
  }

  @Override
  public String getText() {
    return predicate.getText()+"?"+trueExpression.getText()+":"+falseExpression.getText();
  }

}
