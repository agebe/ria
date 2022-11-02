package org.rescript.run;

import org.rescript.symbol.SymbolTable;
import org.rescript.value.Value;

public class ScriptRunner {

  private ScriptContext ctx;

  public ScriptRunner(SymbolTable symbols) {
    super();
    ctx = new ScriptContext(symbols);
    symbols.getScriptSymbols().setCtx(ctx);
  }

  public Value run() {
    ctx.getSymbols().getScriptSymbols().getMain().execute(ctx);
    return ctx.getLastResult();
  }

}
