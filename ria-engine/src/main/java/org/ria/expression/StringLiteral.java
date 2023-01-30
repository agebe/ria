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
import org.ria.run.ScriptContext;
import org.ria.value.ObjValue;
import org.ria.value.Value;

public class StringLiteral implements Expression {

  private String literal;

  private String unescaped;

  private Value val;

  public StringLiteral(String literal) {
    super();
    this.literal = literal;
    this.unescaped = StringEscapeUtils.unescapeJava(literal).intern();
    val = new ObjValue(String.class, unescaped);
  }

  public String getLiteral() {
    return literal;
  }

  public String getUnescaped() {
    return unescaped;
  }

  @Override
  public String toString() {
    return "StringLiteral [literal=" + literal + "]";
  }

  @Override
  public Value eval(ScriptContext ctx) {
    return val;
  }

  @Override
  public String getText() {
    return literal;
  }

}
