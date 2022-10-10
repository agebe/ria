package org.rescript.statement;

import java.util.ArrayList;
import java.util.List;

import org.rescript.run.ScriptContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockStatement implements ContainerStatement {

  private static final Logger log = LoggerFactory.getLogger(BlockStatement.class);

  private List<Statement> statements = new ArrayList<>();

  @Override
  public void execute(ScriptContext ctx) {
    for(Statement s : statements) {
      if(ctx.isReturnFlag()) {
        break;
      }
      s.execute(ctx);
    }
  }

  @Override
  public void addStatement(Statement statement) {
    log.debug("adding '{}' to block", statement);
    statements.add(statement);
  }

  @Override
  public String toString() {
    return "BlockStatement [statements=" + statements + "]";
  }

}
