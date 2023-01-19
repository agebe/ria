package org.ria.statement;

import org.ria.run.ScriptContext;

public class ImportStaticStatement extends AbstractStatement implements Statement {

  private String imp;

  public ImportStaticStatement(int lineNumber, String imp) {
    super(lineNumber);
    this.imp = imp;
  }

  @Override
  public void execute(ScriptContext ctx) {
    ctx.getSymbols().getJavaSymbols().addStaticImport(imp);
  }

}
