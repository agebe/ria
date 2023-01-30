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

import org.apache.commons.text.StringEscapeUtils;
import org.ria.ScriptException;
import org.ria.run.ScriptContext;
import org.ria.value.CharValue;
import org.ria.value.Value;

public class CharLiteral implements Expression {

  private String literal;

  private Value val;

  public CharLiteral(String literal) {
    super();
    this.literal = literal;
    String s = StringEscapeUtils.unescapeJava(literal).intern();
    if(s.length() == 1) {
      val = new CharValue(s.charAt(0));
    } else {
      throw new ScriptException("char literal '%s' requires single character but has '%s' "
          .formatted(literal, literal.length()));
    }
  }

  @Override
  public Value eval(ScriptContext ctx) {
    return val;
  }

  @Override
  public String toString() {
    return "CharLiteral [literal=" + literal + ", val=" + val + "]";
  }

}
