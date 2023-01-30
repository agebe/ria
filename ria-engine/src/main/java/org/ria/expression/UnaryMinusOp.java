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
import org.ria.value.DoubleValue;
import org.ria.value.FloatValue;
import org.ria.value.IntValue;
import org.ria.value.LongValue;
import org.ria.value.ShortValue;
import org.ria.value.Value;

public class UnaryMinusOp implements Expression {

  private Expression expr;

  public UnaryMinusOp(Expression expr) {
    super();
    this.expr = expr;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value val = expr.eval(ctx);
    if(val.isChar()) {
      val = new IntValue(val.toInt());
    }
    if(val.isNumber()) {
      if(val.isDouble()) {
        return new DoubleValue(-val.toDouble());
      } else if(val.isFloat()) {
        return new FloatValue(-val.toFloat());
      } else if(val.isLong()) {
        return new LongValue(-val.toLong());
      } else if(val.isInteger()) {
        return new IntValue(-val.toInt());
      } else if(val.isShort()) {
        return new ShortValue(-val.toShort());
      } else if(val.isByte()) {
        return new ByteValue(-val.toByte());
      } else {
        throw new ScriptException("unsupported number type: " + val.type());
      }
    } else {
      throw new ScriptException("unary minus requires number, " + val);
    }
  }

  @Override
  public String getText() {
    return "-" + expr.getText();
  }

  @Override
  public String toString() {
    return getText();
  }

}
