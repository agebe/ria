package org.ria.statement;

import java.util.ArrayList;
import java.util.List;

import org.ria.run.ScriptContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockStatement extends AbstractStatement implements ContainerStatement {

  private static final Logger log = LoggerFactory.getLogger(BlockStatement.class);

  private List<Statement> statements = new ArrayList<>();

  private boolean root;

  public BlockStatement(int line) {
    this(line, false);
  }

  public BlockStatement(int line, boolean root) {
    super(line);
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
        ctx.getCurrentFrame().setLine(s.getLineNumber());
        if(ctx.isExit()) {
          break;
        }
        if(ctx.isReturnFlag()) {
          log.debug("return flag set, break out of block");
          break;
        }
        Breakable b = ctx.getCurrentFrame().peekBreakable();
        if((b!=null) && b.isBreak()) {
          break;
        }
        Continueable c = ctx.getCurrentFrame().peekContinueable();
        if((c!=null) && c.isContinue()) {
          break;
        }
        log.debug("execute statement '{}'", s);
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
