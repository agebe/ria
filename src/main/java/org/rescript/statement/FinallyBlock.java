package org.rescript.statement;

import org.rescript.parser.ParseItem;
import org.rescript.run.ScriptContext;

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
