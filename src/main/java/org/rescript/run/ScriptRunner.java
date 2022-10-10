package org.rescript.run;

import org.rescript.symbol.SymbolTable;
import org.rescript.value.Value;

public class ScriptRunner {

  private ScriptContext ctx;

  public ScriptRunner(SymbolTable symbols) {
    super();
    ctx = new ScriptContext(symbols);
  }

  public Value run() {
    ctx.getSymbols().getScriptSymbols().getEntryPoint().execute(ctx);
    return ctx.getLastResult();
  }

}
