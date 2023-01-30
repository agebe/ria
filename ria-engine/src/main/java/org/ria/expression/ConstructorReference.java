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
import org.ria.value.ConstructorValue;
import org.ria.value.Value;

public class ConstructorReference implements TargetExpression {

  private org.ria.parser.Type type;

  public ConstructorReference(org.ria.parser.Type type) {
    super();
    this.type = type;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    return new ConstructorValue(type.resolveBaseType(ctx), type.getDim());
  }

  @Override
  public Value eval(ScriptContext ctx, Value target) {
    Class<?> cls = ctx.getSymbols().getJavaSymbols().resolveType(target.type().getName()+"."+type.getName());
    return new ConstructorValue(cls, type.getDim());
  }

}
