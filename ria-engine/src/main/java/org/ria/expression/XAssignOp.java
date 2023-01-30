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
import org.ria.symbol.VarSymbol;
import org.ria.value.Value;

public abstract class XAssignOp implements Expression {

  private Identifier identifier;

  private Expression expression;

  public XAssignOp(Identifier identifier, Expression expression) {
    super();
    this.identifier = identifier;
    this.expression = expression;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    VarSymbol variable = ctx.getSymbols()
        .getScriptSymbols()
        .resolveVar(identifier.getIdent());
    if(variable == null) {
      throw new ScriptException("unknown variable '%s'".formatted(identifier.getIdent()));
    }
    Value v1 = variable.getVal();
    Value v2 = expression.eval(ctx);
    Value result = doOp(v1, v2);
    variable.setVal(result);
    return result;
  }

  protected abstract Value doOp(Value v1, Value v2);

}
