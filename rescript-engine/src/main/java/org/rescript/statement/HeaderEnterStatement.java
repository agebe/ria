package org.rescript.statement;

import org.rescript.dependency.Dependencies;
import org.rescript.dependency.Repositories;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

public class HeaderEnterStatement extends AbstractStatement {

  public static final String DEPENDENCIES = "dependencies";
  public static final String REPOSITORIES = "repositories";

  private Repositories repos;

  public HeaderEnterStatement(int lineNumber, String defaultMavenRepo) {
    super(lineNumber);
    this.repos = new Repositories(defaultMavenRepo);
  }

  @Override
  public void execute(ScriptContext ctx) {
    if(!ctx.getSymbols().getScriptSymbols().isDefined(DEPENDENCIES)) {
      ctx.getSymbols().getScriptSymbols().defineOrAssignVarRoot(
          DEPENDENCIES, Value.of(new Dependencies()));
    }
    if(!ctx.getSymbols().getScriptSymbols().isDefined(REPOSITORIES)) {
      ctx.getSymbols().getScriptSymbols().defineOrAssignVarRoot(
          REPOSITORIES, Value.of(repos));
    }
  }

}
