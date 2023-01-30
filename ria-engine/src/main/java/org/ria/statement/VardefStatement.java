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

import java.util.List;

import org.ria.parser.Type;
import org.ria.run.ScriptContext;

public class VardefStatement extends AbstractStatement implements Statement {

  private List<VarDef> vars;

  private Type type;

  public VardefStatement(int lineNumber, List<VarDef> vars, Type type) {
    super(lineNumber);
    this.vars = vars;
    this.type = type;
  }

  @Override
  public void execute(ScriptContext ctx) {
    vars.forEach(v -> v.execute(ctx, type));
  }

  @Override
  public String toString() {
    return "VardefStatement [vars=" + vars + ", type=" + type + "]";
  }

}
