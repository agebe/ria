package org.rescript.parser;

import org.rescript.antlr.ScriptParser.ExprContext;

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
