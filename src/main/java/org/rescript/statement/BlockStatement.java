package org.rescript.statement;

import java.util.ArrayList;
import java.util.List;

import org.rescript.run.ScriptContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockStatement implements ContainerStatement {

  private static final Logger log = LoggerFactory.getLogger(BlockStatement.class);

  private List<Statement> statements = new ArrayList<>();

  private boolean root;

  public BlockStatement() {
    this(false);
  }

  public BlockStatement(boolean root) {
    super();
    this.root = root;
  }

  @Override
  public void execute(ScriptContext ctx) {
    log.debug("execute block");
    try {
      if(!root) {
        ctx.getSymbols().getScriptSymbols().enterScope();
      }
      for(Statement s : statements) {
        if(ctx.isReturnFlag()) {
          break;
        }
        s.execute(ctx);
      }
    } finally {
      if(!root) {
        ctx.getSymbols().getScriptSymbols().exitScope();
      }
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
