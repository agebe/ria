package org.rescript.statement;

import org.rescript.run.ScriptContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionAlias implements Statement {

  private static final Logger log = LoggerFactory.getLogger(FunctionAlias.class);

  private String alias;

  private String target;

  public FunctionAlias(String alias, String target) {
    super();
    this.alias = alias;
    this.target = target;
  }

  @Override
  public void execute(ScriptContext ctx) {
    log.debug("execute function alias '{}', target '{}'", alias, target);
    ctx.getSymbols().getJavaSymbols().addFunctionAlias(alias, target);
  }

}
