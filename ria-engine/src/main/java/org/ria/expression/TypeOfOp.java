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
import org.ria.symbol.SymbolNotFoundException;
import org.ria.value.ObjValue;
import org.ria.value.Value;

public class TypeOfOp implements Expression {

  private Expression expr;

  public TypeOfOp(Expression expr) {
    super();
    this.expr = expr;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    try {
      Value val = expr.eval(ctx);
      return new ObjValue(String.class, val!=null?val.typeOf():"undefined");
    } catch(SymbolNotFoundException e) {
      return new ObjValue(String.class, "undefined");
    }
  }

}
