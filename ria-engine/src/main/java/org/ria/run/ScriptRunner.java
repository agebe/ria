package org.ria.run;

import org.ria.symbol.SymbolTable;
import org.ria.value.Value;

public class ScriptRunner {

  private ScriptContext ctx;

  public ScriptRunner(SymbolTable symbols) {
    super();
    ctx = new ScriptContext(symbols);
    symbols.getScriptSymbols().setCtx(ctx);
  }

  public Value run() {
    ctx.getSymbols().getScriptSymbols().getMain().executeFunction(ctx);
    return ctx.getLastResult();
  }

}
