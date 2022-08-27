package io.github.agebe.script;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

public class MyParseTreeListener implements ParseTreeListener {

  @Override
  public void visitTerminal(TerminalNode node) {
//    System.out.println("visitTerminal " + node);
  }

  @Override
  public void visitErrorNode(ErrorNode node) {
//    System.out.println("visitErrorNode " + node);
  }

  @Override
  public void enterEveryRule(ParserRuleContext ctx) {
//    System.out.println("enterEveryRule " + ctx );
  }

  @Override
  public void exitEveryRule(ParserRuleContext ctx) {
//    System.out.println("exitEveryRule " + ctx);
  }

}
