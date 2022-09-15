package org.rescript.parser;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;
import org.rescript.ScriptException;
import org.rescript.SyntaxException;
import org.rescript.antlr.ScriptListener;
import org.rescript.antlr.ScriptParser.AssignmentContext;
import org.rescript.antlr.ScriptParser.AssignmentOpContext;
import org.rescript.antlr.ScriptParser.BlockContext;
import org.rescript.antlr.ScriptParser.BoolLiteralContext;
import org.rescript.antlr.ScriptParser.EmptyStmtContext;
import org.rescript.antlr.ScriptParser.ExprContext;
import org.rescript.antlr.ScriptParser.ExprStmtContext;
import org.rescript.antlr.ScriptParser.FcallContext;
import org.rescript.antlr.ScriptParser.FloatLiteralContext;
import org.rescript.antlr.ScriptParser.FnameContext;
import org.rescript.antlr.ScriptParser.FparamContext;
import org.rescript.antlr.ScriptParser.FparamsContext;
import org.rescript.antlr.ScriptParser.IdentContext;
import org.rescript.antlr.ScriptParser.IfElseStmtContext;
import org.rescript.antlr.ScriptParser.IfStmtContext;
import org.rescript.antlr.ScriptParser.IntLiteralContext;
import org.rescript.antlr.ScriptParser.LiteralContext;
import org.rescript.antlr.ScriptParser.ReturnStmtContext;
import org.rescript.antlr.ScriptParser.ScriptContext;
import org.rescript.antlr.ScriptParser.StmtContext;
import org.rescript.antlr.ScriptParser.StrLiteralContext;
import org.rescript.antlr.ScriptParser.VardefStmtContext;
import org.rescript.expression.AssignmentOperator;
import org.rescript.expression.BoolLiteral;
import org.rescript.expression.Expression;
import org.rescript.expression.FloatLiteral;
import org.rescript.expression.FunctionCall;
import org.rescript.expression.Identifier;
import org.rescript.expression.IntLiteral;
import org.rescript.expression.StringLiteral;
import org.rescript.statement.BlockStatement;
import org.rescript.statement.EmptyStatement;
import org.rescript.statement.ExpressionStatement;
import org.rescript.statement.IfStatement;
import org.rescript.statement.ReturnStatement;
import org.rescript.statement.Statement;
import org.rescript.statement.VardefStatement;
import org.rescript.symbol.SymbolTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParserListener implements ScriptListener {

  private static final Logger log = LoggerFactory.getLogger(ParserListener.class);

  private Deque<ParseItem> stack = new ArrayDeque<>();

  public ParserListener() {
    stack.push(new BlockStatement());
  }

  @Override
  public void visitTerminal(TerminalNode node) {
    log.debug("visit terminal '{}'", node.getSymbol().getText());
    stack.push(new Terminal(node.getSymbol()));
  }

  @Override
  public void visitErrorNode(ErrorNode node) {
    log.debug("visit error node '{}'", node);
  }

  @Override
  public void enterEveryRule(ParserRuleContext ctx) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void exitEveryRule(ParserRuleContext ctx) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void enterScript(ScriptContext ctx) {
    log.debug("enter script");
  }

  @Override
  public void exitScript(ScriptContext ctx) {
    if(stack.size() != 1) {
      log.warn("stack should have single item but has '{}', '{}'", stack.size(), stack);
    }
    log.debug("parse done");
  }

  @Override
  public void enterStmt(StmtContext ctx) {
    log.debug("enterStmt '{}'", ctx.getText());
  }

  @Override
  public void exitStmt(StmtContext ctx) {
    log.debug("exitStmt '{}'", ctx.getText());
    log.debug("'{}'", stack);
    Statement s = popStatement();
    findMostRecentStatement().addStatement(s);
  }

  private Statement findMostRecentStatement() {
    Iterator<ParseItem> iter = stack.iterator();
    while(iter.hasNext()) {
      ParseItem p = iter.next();
      if(p instanceof Statement) {
        return (Statement)p;
      }
    }
    return null;
  }

  @Override
  public void enterReturnStmt(ReturnStmtContext ctx) {
    log.debug("enter return stmt '{}'", ctx.getText());
  }

  @Override
  public void exitReturnStmt(ReturnStmtContext ctx) {
    log.debug("exit return stmt '{}'", ctx.getText());
    popSemi();
    Expression expr = null;
    if(stack.getFirst() instanceof Expression) {
      expr = popExpression();
    }
    popTerminal("return");
    stack.push(new ReturnStatement(expr));
  }

  @Override
  public void enterAssignment(AssignmentContext ctx) {
    log.debug("enter assign '{}'", ctx.getText());
  }

  @Override
  public void exitAssignment(AssignmentContext ctx) {
    log.debug("exit assign '{}'", ctx.getText());
  }

  @Override
  public void enterExpr(ExprContext ctx) {
    log.debug("enter expr '{}'", ctx.getText());
    // push an expression start marker on the stack
    // so we know how far to go back on exitExpr
    stack.push(new ExpressionStartMarker(ctx));
  }

  @Override
  public void exitExpr(ExprContext ctx) {
    log.debug("exit expr '{}'", ctx.getText());
    new ExpressionParser(ctx, stack).parse();
  }

  @Override
  public void enterFcall(FcallContext ctx) {
    log.debug("enter fcall '{}'", ctx.getText());
  }

  @Override
  public void exitFcall(FcallContext ctx) {
    log.debug("exit fcall '{}'", ctx.getText());
    // get rid of right parenthesis
    String rp = popTerminal().getToken().getText();
    if(!StringUtils.equals(rp, ")")) {
      fail("expected right parenthesis but got " + rp);
    }
    LinkedList<FunctionParameter> params = new LinkedList<>();
    for(;;) {
      ParseItem item = stack.pop();
      if(item instanceof FunctionParameter) {
        FunctionParameter param = (FunctionParameter)item;
        params.addFirst(param);
      } else if(item instanceof FunctionName) {
        FunctionName name = (FunctionName)item;
        FunctionCall fcall = new FunctionCall(name, params, null);
        log.debug("push fcall '{}' to stack", fcall);
        stack.push(fcall);
        return;
      } else {
        fail("unexpected item on stack for function call " + item);
      }
    }
  }

  @Override
  public void enterFname(FnameContext ctx) {
    log.debug("enter fname '{}'", ctx.getText());
  }

  @Override
  public void exitFname(FnameContext ctx) {
    log.debug("exit fname '{}'", ctx.getText());
    stack.push(new FunctionName(popTerminal().getToken().getText()));
  }

  @Override
  public void enterFparams(FparamsContext ctx) {
    log.debug("enter fparams '{}'", ctx.getText());
    // get rid of initial left parenthesis
    String s = popTerminal().getToken().getText();
    if(!StringUtils.equals(s, "(")) {
      fail("expected left parenthesis " + s);
    }
  }

  @Override
  public void exitFparams(FparamsContext ctx) {
    log.debug("exit fparams '{}'", ctx.getText());
  }

  @Override
  public void enterFparam(FparamContext ctx) {
    log.debug("enter fparam '{}'", ctx.getText());
 // get rid of initial comma separating parameters, this does not work for the first parameter though
    ParseItem item = stack.peek();
    if((item instanceof Terminal) && ((Terminal) item).getToken().getText().equals(",")) {
      popTerminal();
    }
  }

  @Override
  public void exitFparam(FparamContext ctx) {
    log.debug("exit fparam '{}'", ctx.getText());
    stack.push(new FunctionParameter((Expression)stack.pop()));
  }

  @Override
  public void enterLiteral(LiteralContext ctx) {
    log.debug("enter literal '{}'", ctx.getText());
  }

  @Override
  public void exitLiteral(LiteralContext ctx) {
    log.debug("exit literal '{}'", ctx.getText());
  }

  @Override
  public void enterIdent(IdentContext ctx) {
    log.debug("enter ident '{}'", ctx.getText());
  }

  @Override
  public void exitIdent(IdentContext ctx) {
    log.debug("exit ident '{}'", ctx.getText());
    stack.push(new Identifier(popTerminal().getToken().getText()));
  }

  private void fail(String msg) {
    throw new ScriptException(msg);
  }

  private Terminal popTerminal() {
    ParseItem terminal = stack.pop();
    if(!(terminal instanceof Terminal)) {
      fail("expected terminal but got '%s'".formatted(terminal));
    }
    return (Terminal)terminal;
  }

  private Terminal popTerminal(String expected) {
    Terminal t = popTerminal();
    if(t.getText().equals(expected)) {
      return t;
    } else {
      throw new ScriptException("expected terminal '%s' but got '%s'".formatted(expected, t.getText()));
    }
  }

  private void popSemi() {
    popTerminal(";");
  }

  private Expression popExpression() {
    return (Expression)stack.pop();
  }

  private Identifier popIdentifier() {
    return (Identifier)stack.pop();
  }

  private Statement popStatement() {
    return (Statement)stack.pop();
  }

  public SymbolTable getSymbols() {
    return new SymbolTable((Statement)stack.getLast());
  }

  @Override
  public void enterStrLiteral(StrLiteralContext ctx) {
    log.debug("enter strLiteral '{}'", ctx.getText());
  }

  @Override
  public void exitStrLiteral(StrLiteralContext ctx) {
    log.debug("exit strLiteral '{}'", ctx.getText());
    Terminal t = (Terminal)stack.pop();
    String s = t.getToken().getText();
    if(StringUtils.startsWith(s, "\"") && StringUtils.endsWith(s, "\"")) {
      if(s.length() == 1) {
        fail("invalid string literal " + s);
      }
      stack.push(new StringLiteral(StringUtils.substring(s, 1, s.length()-1)));
    } else {
      fail("unsupported literal " + s);
    }
  }

  @Override
  public void enterBoolLiteral(BoolLiteralContext ctx) {
    log.debug("enter boolLiteral '{}'", ctx.getText());
  }

  @Override
  public void exitBoolLiteral(BoolLiteralContext ctx) {
    log.debug("exit boolLiteral '{}'", ctx.getText());
    stack.push(new BoolLiteral(popTerminal().getText()));
  }

  @Override
  public void enterFloatLiteral(FloatLiteralContext ctx) {
    log.debug("enter floatLiteral '{}'", ctx.getText());
  }

  @Override
  public void exitFloatLiteral(FloatLiteralContext ctx) {
    log.debug("exit floatLiteral '{}'", ctx.getText());
    stack.push(new FloatLiteral(popTerminal().getText()));
  }

  @Override
  public void enterIntLiteral(IntLiteralContext ctx) {
    log.debug("enter intLiteral '{}'", ctx.getText());
  }

  @Override
  public void exitIntLiteral(IntLiteralContext ctx) {
    log.debug("exit intLiteral '{}'", ctx.getText());
    stack.push(new IntLiteral(popTerminal().getText()));
  }

  @Override
  public void enterAssignmentOp(AssignmentOpContext ctx) {
    log.debug("enter AssignmentOp '{}'", ctx.getText());
  }

  @Override
  public void exitAssignmentOp(AssignmentOpContext ctx) {
    log.debug("exit AssignmentOp '{}'", ctx.getText());
    Expression expr = popExpression();
    popTerminal("=");
    Identifier ident = popIdentifier();
    stack.push(new AssignmentOperator(ident, expr));
  }

  @Override
  public void enterEmptyStmt(EmptyStmtContext ctx) {
    log.debug("enterEmptyStmt '{}'", ctx.getText());
  }

  @Override
  public void exitEmptyStmt(EmptyStmtContext ctx) {
    log.debug("exitEmptyStmt '{}'", ctx.getText());
    popSemi();
    stack.push(new EmptyStatement());
  }

  @Override
  public void enterExprStmt(ExprStmtContext ctx) {
    log.debug("enterExprStmt '{}'", ctx.getText());
  }

  @Override
  public void exitExprStmt(ExprStmtContext ctx) {
    log.debug("exitExprStmt '{}'", ctx.getText());
    popSemi();
    stack.push(new ExpressionStatement(popExpression()));
  }

  @Override
  public void enterVardefStmt(VardefStmtContext ctx) {
    log.debug("enterVardefStmt '{}'", ctx.getText());
  }

  @Override
  public void exitVardefStmt(VardefStmtContext ctx) {
    log.debug("exitVardefStmt '{}'", ctx.getText());
    popSemi();
    ParseItem pi1 = stack.pop();
    ParseItem pi2 = stack.pop();
    if(pi2 instanceof Terminal) {
      String t2 = ((Terminal)pi2).getText();
      if("var".equals(t2)) {
        Identifier i1 = (Identifier)pi1;
        stack.push(new VardefStatement(i1.getIdent(), null));
      } else if( "=".equals(t2)) {
        ParseItem pi3 = stack.pop();
        popTerminal("var");
        Identifier i3 = (Identifier)pi3;
        stack.push(new VardefStatement(i3.getIdent(), (Expression)pi1));
      }
    } else {
      throw new SyntaxException("expected terminal in vardef but got '%s'".formatted(pi2));
    }
  }

  @Override
  public void enterBlock(BlockContext ctx) {
    log.debug("enterBlock '{}'", ctx.getText());
    stack.push(new BlockStatement());
  }

  @Override
  public void exitBlock(BlockContext ctx) {
    log.debug("exitBlock '{}'", ctx.getText());
    log.debug("stack '{}'", stack);
    popTerminal("}");
    popTerminal("{");
  }

  @Override
  public void enterIfStmt(IfStmtContext ctx) {
    log.debug("enterIfStmt '{}'", ctx.getText());
    stack.push(new IfStatement());
  }

  @Override
  public void exitIfStmt(IfStmtContext ctx) {
    log.debug("exitIfStmt '{}'", ctx.getText());
    log.debug("stack '{}'", stack);
    popTerminal(")");
    Expression e = popExpression();
    popTerminal("(");
    popTerminal("if");
    IfStatement s = (IfStatement)stack.getFirst();
    s.setExpression(e);
  }

  @Override
  public void enterIfElseStmt(IfElseStmtContext ctx) {
    log.debug("enterIfElseStmt '{}'", ctx.getText());
    stack.push(new IfStatement());
  }

  @Override
  public void exitIfElseStmt(IfElseStmtContext ctx) {
    log.debug("exitIfElseStmt '{}'", ctx.getText());
    popTerminal("else");
    popTerminal(")");
    Expression e = popExpression();
    popTerminal("(");
    popTerminal("if");
    IfStatement s = (IfStatement)stack.getFirst();
    s.setExpression(e);
  }

}
