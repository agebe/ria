package org.rescript.statement;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.rescript.CheckedExceptionWrapper;
import org.rescript.run.ScriptContext;
import org.rescript.util.ExceptionUtils;

public class TryStatement extends AbstractStatement {

  private LinkedList<TryResource> resources;

  private BlockStatement block;

  private List<CatchBlock> catchBlocks;

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

  private Throwable unwrap(Throwable t) {
    if(t instanceof CheckedExceptionWrapper wrapper) {
      return wrapper.getCause();
    } else {
      return t;
    }
  }

  @Override
  public void execute(ScriptContext ctx) {
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
            // TODO remember exception coming out of the catch block (if any) as we might need to add suppressed exceptions
            // from the finally block
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
//      try {
//     // TODO execute the finally block if present 
//      } catch(Throwable t) {
//        
//      }
    }
    if(leavingException != null) {
      ExceptionUtils.wrapCheckedAndThrow(leavingException);;
    }
  }

}
