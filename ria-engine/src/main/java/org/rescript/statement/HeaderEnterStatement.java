package org.rescript.statement;

import java.io.File;

import org.rescript.Options;
import org.rescript.dependency.Dependencies;
import org.rescript.dependency.Repositories;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

public class HeaderEnterStatement extends AbstractStatement {

  public static final String OPTIONS = "options";
  public static final String DEPENDENCIES = "dependencies";
  public static final String REPOSITORIES = "repositories";

  private Repositories repos;

  public HeaderEnterStatement(int lineNumber, String defaultMavenRepo, File cacheBase) {
    super(lineNumber);
    this.repos = new Repositories(defaultMavenRepo, cacheBase);
  }

  @Override
  public void execute(ScriptContext ctx) {
    if(!ctx.getSymbols().getScriptSymbols().isDefined(OPTIONS)) {
      ctx.getSymbols().getScriptSymbols().defineOrAssignVarRoot(
          OPTIONS, Value.of(new Options()));
    }
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
