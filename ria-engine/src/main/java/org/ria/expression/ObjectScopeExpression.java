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

import java.util.List;
import java.util.function.Consumer;

import org.ria.run.ScriptContext;
import org.ria.statement.BlockStatement;
import org.ria.value.Value;
import org.ria.value.VoidValue;

public class ObjectScopeExpression implements Expression {

  private Expression expression;

  private List<Expression> expressions;

  private BlockStatement block;

  public ObjectScopeExpression(Expression expression, List<Expression> expressions, BlockStatement block) {
    super();
    this.expression = expression;
    this.expressions = expressions;
    this.block = block;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public Value eval(ScriptContext ctx) {
    Value v = expression.eval(ctx);
    if(v.isNull()) {
      return v;
    }
    final Object o = v.val();
    try {
      ctx.getSymbols().getScriptSymbols().enterObjectScope(o);
      if(block != null) {
        block.execute(ctx);
      } else {
        expressions.forEach(expr -> {
          Value v2 = expr.eval(ctx);
          if(!VoidValue.VOID.equals(v2)) {
            if(o instanceof Consumer c) {
              c.accept(v2.val());
            }
          }
        });
      }
      return v;
    } finally {
      ctx.getSymbols().getScriptSymbols().exitScope();
    }
  }

}
