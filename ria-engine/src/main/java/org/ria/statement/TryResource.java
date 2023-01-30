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
package org.ria.statement;

import org.ria.ScriptException;
import org.ria.expression.Expression;
import org.ria.expression.Identifier;
import org.ria.parser.ParseItem;
import org.ria.parser.Type;
import org.ria.run.ScriptContext;
import org.ria.value.Value;

public class TryResource implements ParseItem {

  private Type type;

  private Identifier identifier;

  private Expression expression;

  private AutoCloseable closeable;

  public TryResource(Type type, Identifier identifier, Expression expression) {
    super();
    this.type = type;
    this.identifier = identifier;
    this.expression = expression;
  }

  public void execute(ScriptContext ctx) {
    Value v = expression.eval(ctx);
    Object o = v.val();
    if(o == null) {
      closeable = null;
    } else if(o instanceof AutoCloseable c) {
      closeable = c;
    } else {
      throw new ScriptException("try-with-resouce requires AutoCloseable but got '%s'".formatted(v.type()));
    }
    ctx.getSymbols().getScriptSymbols().defineVar(identifier.getIdent(), v, type);
  }

  public void close() throws Exception {
    if(closeable != null) {
      closeable.close();
    }
  }

}
