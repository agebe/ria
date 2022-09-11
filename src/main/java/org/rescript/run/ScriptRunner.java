package org.rescript.run;

import org.rescript.symbol.SymbolTable;
import org.rescript.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptRunner {

  private static final Logger log = LoggerFactory.getLogger(ScriptRunner.class);

  private ScriptContext ctx;

  public ScriptRunner(SymbolTable symbols) {
    super();
    ctx = new ScriptContext(symbols);
  }

  public Value run() {
    while(ctx.getCurrent() != null) {
      log.debug("exec next statement '{}'", ctx.getCurrent());
      ctx.getCurrent().getStmt().execute(ctx);
    }
    log.debug("exit script run");
    return ctx.getLastResult();
  }

//  private AstNode getPath(AstNode node, Value val) {
//    if(Objects.isNull(node.getTrueNode()) && Objects.isNull(node.getFalseNode())) {
//      return null;
//    } else if(Objects.isNull(node.getFalseNode())) {
//      return node.getTrueNode();
//    } else if(Objects.isNull(node.getTrueNode())) {
//      return node.getFalseNode();
//    } else {
//      if(val == null) {
//        throw new ScriptException("no result on statement '%s'".formatted(node.getStmt()));
//      }
//      if(!val.isBoolean()) {
//        throw new ScriptException("no boolean result on statement '%s' but '%s'".formatted(
//            node.getStmt(), val.type()));
//      }
//      return node.next(val.toBoolean());
//    }
//  }

}
