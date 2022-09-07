package org.rescript.parser;

import io.github.agebe.script.antlr.ScriptParser.ExprContext;

public class ExpressionStartMarker implements ParseItem {

  private ExprContext ctx;

  public ExpressionStartMarker(ExprContext ctx) {
    super();
    this.ctx = ctx;
  }

  public ExprContext getCtx() {
    return ctx;
  }

}
