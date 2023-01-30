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

public class BitXorOp extends TripleOp {

  public BitXorOp(Expression exp1, Expression exp2) {
    super(exp1, exp2, "^");
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value v1 = getExp1().eval(ctx);
    Value v2 = getExp2().eval(ctx);
    return xor(v1, v2, getOp());
  }

  static Value xor(Value v1, Value v2, String op) {
    if(v1.isChar() && !v2.isString()) {
      v1 = new IntValue(v1.toInt());
    }
    if(v2.isChar() && !v1.isString()) {
      v2 = new IntValue(v2.toInt());
    }
    if(v1.isNumber() && v2.isNumber()) {
      if(v1.isDouble() || v2.isDouble()) {
        return new LongValue(v1.toLong() ^ v2.toLong());
      } else if(v1.isFloat() || v2.isFloat()) {
        return new LongValue(v1.toLong() ^ v2.toLong());
      } else if(v1.isLong() || v2.isLong()) {
        return new LongValue(v1.toLong() ^ v2.toLong());
      } else if(v1.isInteger() || v2.isInteger()) {
        return new IntValue(v1.toInt() ^ v2.toInt());
      } else if(v1.isShort() || v2.isShort()) {
        return new ShortValue(v1.toShort() ^ v2.toShort());
      } else if(v1.isByte() || v2.isByte()) {
        return new ByteValue(v1.toByte() ^ v2.toByte());
      } else {
        throw new ScriptException("unexpected case on '%s', types '%s' and '%s'"
            .formatted(op, v1.type(), v2.type()));
      }
    } else {
      throw new ScriptException("operation '%s' requires numbers but got '%s' and '%s'"
          .formatted(op, v1.type(), v2.type()));
    }
  }

}
