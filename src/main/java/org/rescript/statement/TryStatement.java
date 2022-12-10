package org.rescript.statement;

import java.util.List;

import org.rescript.CheckedExceptionWrapper;
import org.rescript.run.ScriptContext;

public class TryStatement extends AbstractStatement {

  private BlockStatement block;

  private List<CatchBlock> catchBlocks;

  public TryStatement(int lineNumber) {
    super(lineNumber);
  }

  public BlockStatement getBlock() {
    return block;
  }

  public void setBlock(BlockStatement block) {
    this.block = block;
  }

  public List<CatchBlock> getCatchBlocks() {
    return catchBlocks;
  }

  public void setCatchBlocks(List<CatchBlock> catchBlocks) {
    this.catchBlocks = catchBlocks;
  }

  private Throwable unwrap(Throwable t) {
    if(t instanceof CheckedExceptionWrapper wrapper) {
      return wrapper.getCause();
    } else {
      return t;
    }
  }

  @Override
  public void execute(ScriptContext ctx) {
    try {
      block.execute(ctx);
    } catch(Throwable t) {
      Throwable unwrapped = unwrap(t);
      boolean handled = false;
      if(catchBlocks != null) {
        for(CatchBlock cblock : catchBlocks) {
          if(cblock.handles(ctx, unwrapped)) {
            // TODO remember exception coming out of the catch block (if any) as we might need to add suppressed exceptions
            // from the finally block
            handled = true;
            cblock.execute(ctx, unwrapped);
            break;
          }
        }
      }
      if(!handled) {
        throw t;
      }
    } finally {
//      try {
//     // TODO execute the finally block if present
//      } catch(Throwable t) {
//        
//      }
      // TODO close resources in opposite order they were created. wrap each close call into try catch
    }
  }

}
