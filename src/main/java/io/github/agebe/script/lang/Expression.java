package io.github.agebe.script.lang;

import io.github.agebe.script.antlr.ScriptParser.ExprContext;

public class Expression extends CachedLangItem {

  private ExprContext ctx;

  public Expression(ExprContext ctx) {
    super();
    this.ctx = ctx;
  }

  public ExprContext getCtx() {
    return ctx;
  }

}
