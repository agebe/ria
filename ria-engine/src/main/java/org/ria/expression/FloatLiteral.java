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

import org.apache.commons.lang3.StringUtils;
import org.ria.run.ScriptContext;
import org.ria.value.DoubleValue;
import org.ria.value.FloatValue;
import org.ria.value.Value;

public class FloatLiteral implements Expression {

  private Value value;

  private String literal;

  public FloatLiteral(String literal) {
    super();
    this.literal = literal;
    this.value = parse(literal);
  }

  private Value parse(String literal) {
    if(literal.endsWith("f") || literal.endsWith("F")) {
      return new FloatValue(Float.parseFloat(StringUtils.remove(literal, '_')));
    } else {
      return new DoubleValue(Double.parseDouble(StringUtils.remove(literal, '_')));
    }
  }

  @Override
  public Value eval(ScriptContext ctx) {
    return value;
  }

  @Override
  public String getText() {
    return literal;
  }

}
