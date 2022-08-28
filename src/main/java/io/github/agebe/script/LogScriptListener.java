package io.github.agebe.script;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;

import io.github.agebe.script.antlr.ScriptListener;
import io.github.agebe.script.antlr.ScriptParser.AssignmentContext;
import io.github.agebe.script.antlr.ScriptParser.ExprContext;
import io.github.agebe.script.antlr.ScriptParser.FcallContext;
import io.github.agebe.script.antlr.ScriptParser.FnameContext;
import io.github.agebe.script.antlr.ScriptParser.FparamContext;
import io.github.agebe.script.antlr.ScriptParser.FparamsContext;
import io.github.agebe.script.antlr.ScriptParser.IdentContext;
import io.github.agebe.script.antlr.ScriptParser.LiteralContext;
import io.github.agebe.script.antlr.ScriptParser.ReturnStmtContext;
import io.github.agebe.script.antlr.ScriptParser.ScriptContext;
import io.github.agebe.script.antlr.ScriptParser.StmtContext;
import io.github.agebe.script.antlr.ScriptParser.VarAssignStmtContext;
import io.github.agebe.script.antlr.ScriptParser.VardefContext;

public class LogScriptListener implements ScriptListener {

  private void log(String msg) {
    log(msg, null);
  }

  private void log(String msg, String param) {
    if(StringUtils.isBlank(param)) {
      System.out.println(msg);
    } else {
      System.out.println(msg + " / " + param);
    }
  }

  @Override
  public void visitTerminal(TerminalNode node) {
    log("visit terminal", node.getText());
  }

  @Override
  public void visitErrorNode(ErrorNode node) {
    log("visit error node");
  }

  @Override
  public void enterEveryRule(ParserRuleContext ctx) {
//    log("enter every rule", ctx.getText());
  }

  @Override
  public void exitEveryRule(ParserRuleContext ctx) {
//    log("exit every rule");
  }

  @Override
  public void enterScript(ScriptContext ctx) {
    log("enter script");
  }

  @Override
  public void exitScript(ScriptContext ctx) {
    log("exit script");
  }

  @Override
  public void enterStmt(StmtContext ctx) {
    log("enter stmt");
  }

  @Override
  public void exitStmt(StmtContext ctx) {
    log("exit stmt");
  }

  @Override
  public void enterReturnStmt(ReturnStmtContext ctx) {
    log("enter return stmt");
  }

  @Override
  public void exitReturnStmt(ReturnStmtContext ctx) {
    log("exit return stmt");
  }

  @Override
  public void enterVardef(VardefContext ctx) {
    log("enter vardef");
  }

  @Override
  public void exitVardef(VardefContext ctx) {
    log("exit vardef");
  }

  @Override
  public void enterVarAssignStmt(VarAssignStmtContext ctx) {
    log("enter var assign stmt");
  }

  @Override
  public void exitVarAssignStmt(VarAssignStmtContext ctx) {
    log("exit var assign stmt");
  }

  @Override
  public void enterAssignment(AssignmentContext ctx) {
    log("enter var assign");
  }

  @Override
  public void exitAssignment(AssignmentContext ctx) {
    log("exit var assign");
  }

  @Override
  public void enterExpr(ExprContext ctx) {
    log("enter expr", ctx.getText());
  }

  @Override
  public void exitExpr(ExprContext ctx) {
    log("exit expr", ctx.getText());
  }

  @Override
  public void enterFcall(FcallContext ctx) {
    log("enter fcall");
  }

  @Override
  public void exitFcall(FcallContext ctx) {
    log("exit fcall");
  }

  @Override
  public void enterFname(FnameContext ctx) {
    log("enter fname");
  }

  @Override
  public void exitFname(FnameContext ctx) {
    log("exit fname");
  }

  @Override
  public void enterFparams(FparamsContext ctx) {
    log("enter fparams");
  }

  @Override
  public void exitFparams(FparamsContext ctx) {
    log("exit fparams");
  }

  @Override
  public void enterFparam(FparamContext ctx) {
    log("enter fparam");
  }

  @Override
  public void exitFparam(FparamContext ctx) {
    log("exit fparam");
  }

  @Override
  public void enterLiteral(LiteralContext ctx) {
    log("enter literal");
  }

  @Override
  public void exitLiteral(LiteralContext ctx) {
    log("exit literal");
  }

  @Override
  public void enterIdent(IdentContext ctx) {
    log("enter ident");
  }

  @Override
  public void exitIdent(IdentContext ctx) {
    log("exit ident");
  }

}
