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
import org.ria.value.ByteValue;
import org.ria.value.IntValue;
import org.ria.value.LongValue;
import org.ria.value.ShortValue;
import org.ria.value.Value;

public class BitNotOp implements Expression {

  private Expression expression;

  public BitNotOp(Expression expression) {
    super();
    this.expression = expression;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value v = expression.eval(ctx);
    if(v.isChar()) {
      v = new IntValue(v.toInt());
    }
    if(v.isNumber()) {
      if(v.isDouble()) {
        return new LongValue(~v.toLong());
      } else if(v.isFloat()) {
        return new LongValue(~v.toLong());
      } else if(v.isLong()) {
        return new LongValue(~v.toLong());
      } else if(v.isInteger()) {
        return new IntValue(~v.toInt());
      } else if(v.isShort()) {
        return new ShortValue(~v.toShort());
      } else if(v.isByte()) {
        return new ByteValue(~v.toByte());
      } else {
        throw new ScriptException("unexpected case on bitwise complement, type '%s' not supported"
            .formatted(v.type()));
      }
    } else {
      throw new ScriptException("bitwise complement requires numbers but got '%s'"
          .formatted(v.type()));
    }
  }

}
