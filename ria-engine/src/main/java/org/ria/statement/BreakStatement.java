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
import org.ria.run.ScriptContext;

public class BreakStatement extends AbstractStatement {

  public BreakStatement(int lineNumber) {
    super(lineNumber);
  }

  @Override
  public void execute(ScriptContext ctx) {
    Breakable b = ctx.getCurrentFrame().peekBreakable();
    if(b != null) {
      b.setBreak();
    } else {
      throw new ScriptException("break can only be used inside a loop or switch/case");
    }
  }

}
