package org.ria.statement;

import org.ria.parser.ParseItem;
import org.ria.run.ScriptContext;

public class FinallyBlock implements ParseItem {

  private BlockStatement block;

  public FinallyBlock(BlockStatement block) {
    super();
    this.block = block;
  }

  public void execute(ScriptContext ctx) {
    block.execute(ctx);
  }

}
