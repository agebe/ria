package org.rescript.statement;

import org.rescript.ScriptException;
import org.rescript.expression.Assignment;
import org.rescript.expression.Identifier;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;
import org.rescript.value.VoidValue;

public class VarDef {

  private Identifier ident;

  private Assignment assign;

  public VarDef(Identifier ident) {
    super();
    this.ident = ident;
  }

  public VarDef(Assignment assign) {
    super();
    this.assign = assign;
  }

  public void execute(ScriptContext ctx) {
    if(ident != null) {
      ctx.getSymbols().getScriptSymbols().defineVar(ident.getIdent(), VoidValue.VOID);
    } else if(assign != null) {
      assign.identifiers().forEach(i -> ctx.getSymbols().getScriptSymbols().defineVar(i.getIdent(), VoidValue.VOID));
      Value v = assign.eval(ctx);
      ctx.setLastResult(v);
    } else {
      throw new ScriptException("invalid state, ident and assign null");
    }
  }

}
