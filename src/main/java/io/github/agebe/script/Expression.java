package io.github.agebe.script;

import io.github.agebe.script.antlr.ScriptParser.ExprContext;

public class Expression implements StackItem {

  private ExprContext ctx;

  public Expression(ExprContext ctx) {
    super();
    this.ctx = ctx;
  }

  public ExprContext getCtx() {
    return ctx;
  }

}
