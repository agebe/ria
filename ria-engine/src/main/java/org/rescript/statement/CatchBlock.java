package org.rescript.statement;

import java.util.List;

import org.rescript.parser.ParseItem;
import org.rescript.parser.Type;
import org.rescript.run.ScriptContext;
import org.rescript.value.ObjValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatchBlock implements ParseItem {

  private static final Logger log = LoggerFactory.getLogger(CatchBlock.class);

  private List<Type> exceptionsTypes;

  private String ident;

  private BlockStatement block;

  public CatchBlock(List<Type> exceptionsTypes, String ident, BlockStatement block) {
    super();
    this.exceptionsTypes = exceptionsTypes;
    this.ident = ident;
    this.block = block;
  }

  public List<Type> getExceptionsTypes() {
    return exceptionsTypes;
  }

  public String getIdent() {
    return ident;
  }

  public BlockStatement getBlock() {
    return block;
  }

  public boolean handles(ScriptContext ctx, Throwable t) {
    for(Type type : exceptionsTypes) {
      Class<?> exceptionType = type.resolve(ctx);
      // TODO null check?
      log.debug("'{}' isAssignableFrom '{}': '{}'",
          exceptionType, t.getClass(), exceptionType.isAssignableFrom(t.getClass()));
      if(exceptionType.isAssignableFrom(t.getClass())) {
        return true;
      }
    }
    return false;
  }

  public void execute(ScriptContext ctx, Throwable t) {
    try {
      ctx.getSymbols().getScriptSymbols().enterScope();
      ctx.getSymbols().getScriptSymbols().defineVar(ident, new ObjValue(t.getClass(), t));
      this.block.execute(ctx);
    } finally {
      ctx.getSymbols().getScriptSymbols().exitScope();
    }
  }

}
