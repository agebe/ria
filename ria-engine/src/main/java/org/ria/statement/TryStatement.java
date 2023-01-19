package org.ria.statement;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ria.CheckedExceptionWrapper;
import org.ria.run.ScriptContext;
import org.ria.util.ExceptionUtils;
import org.ria.value.Value;

public class TryStatement extends AbstractStatement {

  private LinkedList<TryResource> resources;

  private BlockStatement block;

  private List<CatchBlock> catchBlocks;

  private FinallyBlock finallyBlock;

  public TryStatement(int lineNumber) {
    super(lineNumber);
  }

  public LinkedList<TryResource> getResources() {
    return resources;
  }

  public void setResources(LinkedList<TryResource> resources) {
    this.resources = resources;
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

  public FinallyBlock getFinallyBlock() {
    return finallyBlock;
  }

  public void setFinallyBlock(FinallyBlock finallyBlock) {
    this.finallyBlock = finallyBlock;
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
    // if the try or catch block throws an exception remember it here so the finally block can
    // add suppressed exceptions
    Throwable leavingException = null;
    try {
      try {
        ctx.getSymbols().getScriptSymbols().enterScope();
        resources.forEach(r -> r.execute(ctx));
        block.execute(ctx);
      } finally {
        // removing the scope here as the resources can no longer be resolved in the
        // catch or finally blocks
        ctx.getSymbols().getScriptSymbols().exitScope();
      }
    } catch(Throwable t) {
      Throwable unwrapped = unwrap(t);
      leavingException = unwrapped;
      boolean handled = false;
      if(catchBlocks != null) {
        for(CatchBlock cblock : catchBlocks) {
          if(cblock.handles(ctx, unwrapped)) {
            handled = true;
            try {
              leavingException = null;
              cblock.execute(ctx, unwrapped);
              break;
            } catch(Throwable t2) {
              leavingException = t2;
              throw t2;
            }
          }
        }
      }
      if(!handled) {
        throw t;
      }
    } finally {
      Value val = ctx.getLastResult();
      boolean returnFlag = ctx.isReturnFlag();
      // clear the return flag otherwise the finally block does not execute
      ctx.setReturnFlag(false);
      if(finallyBlock != null) {
        try {
          finallyBlock.execute(ctx);
        } catch(Throwable t) {
          if(leavingException != null) {
            leavingException.addSuppressed(t);
          } else {
            leavingException = t;
          }
        }
      }
      // restore return flag and last value from above try or catch blocks.
      // this diverges from java, you can't return a value from a finally block
      // finally blocks are meant for cleaning up ...
      ctx.setReturnFlag(returnFlag);
      ctx.setLastResult(val);
      Iterator<TryResource> iter = resources.descendingIterator();
      while(iter.hasNext()) {
        TryResource resource = iter.next();
        try {
          if(resource != null) {
            resource.close();
          }
        } catch(Throwable t) {
          if(leavingException != null) {
            leavingException.addSuppressed(t);
          } else {
            leavingException = t;
          }
        }
      }
    }
    if(leavingException != null) {
      ExceptionUtils.wrapCheckedAndThrow(leavingException);;
    }
  }

}
