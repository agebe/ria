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

import org.ria.ScriptException;
import org.ria.expression.Assignment;
import org.ria.run.ScriptContext;

public class ForInitStatement extends AbstractStatement implements Statement {

  private VardefStatement vardef;

  private List<Assignment> assigments;

  public ForInitStatement(int lineNumber) {
    super(lineNumber);
  }

  public ForInitStatement(int lineNumber, VardefStatement vardef) {
    super(lineNumber);
    this.vardef = vardef;
  }

  public ForInitStatement(int lineNumber, List<Assignment> assigments) {
    super(lineNumber);
    this.assigments = assigments;
  }

  @Override
  public void execute(ScriptContext ctx) {
    if(vardef != null) {
      vardef.execute(ctx);
    } else if(assigments != null) {
      assigments.forEach(a -> a.eval(ctx));
    } else {
      throw new ScriptException("invalid state");
    }
  }

}
