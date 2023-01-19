package org.rescript.statement;

import org.rescript.run.ScriptContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportStatement extends AbstractStatement implements Statement {

  private static final Logger log = LoggerFactory.getLogger(ImportStatement.class);

  private String imp;

  public ImportStatement(int lineNumber, String imp) {
    super(lineNumber);
    this.imp = imp;
  }

  @Override
  public void execute(ScriptContext ctx) {
    log.debug("execute import '{}'", imp);
    ctx.getSymbols().getJavaSymbols().addImport(imp);
  }

}
