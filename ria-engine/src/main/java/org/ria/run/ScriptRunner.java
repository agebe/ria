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
