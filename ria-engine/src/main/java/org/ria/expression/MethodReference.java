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
import org.ria.value.MethodValue;
import org.ria.value.Value;

public class MethodReference implements TargetExpression {

  private String varOrType;

  private String methodName;

  public MethodReference(String varOrType, String methodName) {
    super();
    this.varOrType = varOrType;
    this.methodName = methodName;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value v = ctx.getSymbols().resolveVarOrTypeOrStaticMember(varOrType, ctx);
    return new MethodValue(v.type(), v.val(), methodName);
  }

  @Override
  public Value eval(ScriptContext ctx, Value target) {
    Value v = ctx.getSymbols().resolveVarOrTypeOrStaticMember(target.type().getName()+"."+varOrType, ctx);
    return new MethodValue(v.type(), v.val(), methodName);
  }

}
