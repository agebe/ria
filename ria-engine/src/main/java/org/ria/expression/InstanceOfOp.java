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
import org.ria.parser.TypeOrPrimitive;
import org.ria.run.ScriptContext;
import org.ria.value.BooleanValue;
import org.ria.value.Value;

public class InstanceOfOp implements Expression {

  private Expression expr1;

  private TypeOrPrimitive type;

  private Identifier bind;

  public InstanceOfOp(Expression expr1, TypeOrPrimitive type, Identifier bind) {
    super();
    this.expr1 = expr1;
    this.type = type;
    this.bind = bind;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    boolean isInstanceOf = false;
    Value v1 = expr1.eval(ctx);
    if(v1 == null) {
      throw new ScriptException("left operand failed to evaluate '%s'".formatted(expr1.getText()));
    }
    if(type.getType().isPrimitive()) {
      if(type.getType().isDouble()) {
        isInstanceOf = v1.type() == double.class;
      } else if(type.getType().isFloat()) {
        isInstanceOf = v1.type() == float.class;
      } else if(type.getType().isLong()) {
        isInstanceOf = v1.type() == long.class;
      } else if(type.getType().isInt()) {
        isInstanceOf = v1.type() == int.class;
      } else if(type.getType().isBoolean()) {
        isInstanceOf = v1.type() == boolean.class;
      } else if(type.getType().isChar()) {
        isInstanceOf = v1.type() == char.class;
      } else if(type.getType().isByte()) {
        isInstanceOf = v1.type() == byte.class;
      } else if(type.getType().isShort()) {
        isInstanceOf = v1.type() == short.class;
      } else if(type.getType().isVoid()) {
        isInstanceOf = v1.type() == void.class;
      } else {
        throw new ScriptException("unknown primitive type '%s'".formatted(type.getType()));
      }
    } else {
      Class<?> cls = type.getType().resolve(ctx);
      isInstanceOf = cls.isAssignableFrom(v1.type());
    }
    if(isInstanceOf && (this.bind != null)) {
      ctx.getSymbols().getScriptSymbols().defineVar(this.bind.getIdent(), v1);
    }
    return BooleanValue.of(isInstanceOf);
  }

}
