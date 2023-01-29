package org.ria.statement;

import org.ria.ScriptException;
import org.ria.expression.Assignment;
import org.ria.expression.Identifier;
import org.ria.parser.Type;
import org.ria.run.ScriptContext;
import org.ria.value.Value;

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

  public void execute(ScriptContext ctx, Type type) {
    // type can be null if e.g. the variable was declared via 'var' terminal.
    // in this case the variable can freely change it's type (as opposed to java)
    // otherwise the type is fixed and can't change over it's lifetime (same as in java)
    if(ident != null) {
      ctx.getSymbols().getScriptSymbols().defineVarUninitialized(ident.getIdent(), type);
    } else if(assign != null) {
      assign.identifiers().forEach(
          i -> ctx.getSymbols().getScriptSymbols().defineVarUninitialized(i.getIdent(), type));
      Value v = assign.eval(ctx);
      ctx.setLastResult(v);
    } else {
      throw new ScriptException("invalid state, ident and assign null");
    }
  }

  @Override
  public String toString() {
    return "VarDef [ident=" + ident + ", assign=" + assign + "]";
  }

}
