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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.ria.run.ScriptContext;
import org.ria.value.Value;

public class ListLiteral implements Expression {

  private List<Expression> expressions;

  public ListLiteral(List<Expression> expressions) {
    super();
    this.expressions = expressions;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    List<?> l = expressions.stream()
        .map(expr -> expr.eval(ctx).val())
        .collect(Collectors.toCollection(ArrayList::new));
    return Value.of(List.class, l);
  }

}
