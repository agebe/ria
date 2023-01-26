package org.ria.statement;

import java.io.File;

import org.ria.Options;
import org.ria.dependency.Dependencies;
import org.ria.dependency.Repositories;
import org.ria.run.ScriptContext;
import org.ria.value.Value;

public class HeaderEnterStatement extends AbstractStatement {

  public static final String OPTIONS = "options";
  public static final String DEPENDENCIES = "dependencies";
  public static final String REPOSITORIES = "repositories";

  private Repositories repos;

  private Options options;

  public HeaderEnterStatement(int lineNumber, String defaultMavenRepo, File cacheBase, Options options) {
    super(lineNumber);
    this.repos = new Repositories(defaultMavenRepo, cacheBase);
    this.options = options;
  }

  @Override
  public void execute(ScriptContext ctx) {
    if(!ctx.getSymbols().getScriptSymbols().isDefined(OPTIONS)) {
      ctx.getSymbols().getScriptSymbols().defineOrAssignVarRoot(
          OPTIONS, Value.of(options));
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
