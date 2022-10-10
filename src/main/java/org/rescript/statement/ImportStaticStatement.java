package org.rescript.statement;

import org.rescript.run.ScriptContext;

public class ImportStaticStatement implements Statement {

  private String imp;

  public ImportStaticStatement(String imp) {
    super();
    this.imp = imp;
  }

  @Override
  public void execute(ScriptContext ctx) {
    ctx.getSymbols().getJavaSymbols().addStaticImport(imp);
  }

}
