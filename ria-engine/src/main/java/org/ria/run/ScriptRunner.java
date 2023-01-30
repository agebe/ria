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
package org.ria.run;

import org.ria.Features;
import org.ria.firewall.Firewall;
import org.ria.symbol.SymbolTable;
import org.ria.value.Value;

public class ScriptRunner {

  private ScriptContext ctx;

  public ScriptRunner(SymbolTable symbols, Firewall firewall, Features features) {
    super();
    ctx = new ScriptContext(symbols, firewall, features);
    symbols.getScriptSymbols().setCtx(ctx);
  }

  public Value run() {
    ctx.getSymbols().getScriptSymbols().getMain().executeFunction(ctx);
    return ctx.getLastResult();
  }

}
