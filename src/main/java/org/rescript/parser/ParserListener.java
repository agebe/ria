package org.rescript.parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
import org.rescript.antlr.ScriptParser.CcallContext;
import org.rescript.antlr.ScriptParser.CnameContext;
import org.rescript.antlr.ScriptParser.DottedIdentContext;
import org.rescript.antlr.ScriptParser.EmptyStmtContext;
import org.rescript.antlr.ScriptParser.ExprContext;
import org.rescript.antlr.ScriptParser.ExprStmtContext;
import org.rescript.antlr.ScriptParser.FcallContext;
import org.rescript.antlr.ScriptParser.FloatLiteralContext;
import org.rescript.antlr.ScriptParser.FnameContext;
import org.rescript.antlr.ScriptParser.ForIncContext;
import org.rescript.antlr.ScriptParser.ForInitContext;
import org.rescript.antlr.ScriptParser.ForStmtContext;
import org.rescript.antlr.ScriptParser.ForTermContext;
import org.rescript.antlr.ScriptParser.FparamContext;
import org.rescript.antlr.ScriptParser.FparamsContext;
import org.rescript.antlr.ScriptParser.FunctionAliasContext;
import org.rescript.antlr.ScriptParser.HeaderContext;
import org.rescript.antlr.ScriptParser.IdentContext;
import org.rescript.antlr.ScriptParser.IfElseStmtContext;
import org.rescript.antlr.ScriptParser.IfStmtContext;
import org.rescript.antlr.ScriptParser.ImportStmtContext;
import org.rescript.antlr.ScriptParser.ImportTypeContext;
import org.rescript.antlr.ScriptParser.IntLiteralContext;
import org.rescript.antlr.ScriptParser.LiteralContext;
import org.rescript.antlr.ScriptParser.NullLiteralContext;
import org.rescript.antlr.ScriptParser.ReturnStmtContext;
import org.rescript.antlr.ScriptParser.ScriptContext;
import org.rescript.antlr.ScriptParser.StmtContext;
import org.rescript.antlr.ScriptParser.StrLiteralContext;
import org.rescript.antlr.ScriptParser.VardefStmtContext;
import org.rescript.antlr.ScriptParser.WhileStmtContext;
import org.rescript.expression.AssignmentOperator;
import org.rescript.expression.BoolLiteral;
import org.rescript.expression.Expression;
import org.rescript.expression.FloatLiteral;
import org.rescript.expression.FunctionCall;
import org.rescript.expression.Identifier;
import org.rescript.expression.IntLiteral;
import org.rescript.expression.NewOp;
import org.rescript.expression.NullLiteral;
import org.rescript.expression.StringLiteral;
import org.rescript.statement.BlockStatement;
import org.rescript.statement.ContainerStatement;
import org.rescript.statement.EmptyStatement;
import org.rescript.statement.ExpressionStatement;
import org.rescript.statement.ForInitStatement;
import org.rescript.statement.ForStatement;
import org.rescript.statement.ForStatementBuilder;
import org.rescript.statement.FunctionAlias;
import org.rescript.statement.IfStatement;
import org.rescript.statement.ImportStatement;
import org.rescript.statement.ImportStaticStatement;
import org.rescript.statement.ReturnStatement;
import org.rescript.statement.Statement;
import org.rescript.statement.VarDef;
import org.rescript.statement.VardefStatement;
import org.rescript.statement.WhileStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParserListener implements ScriptListener {

  private static final Logger log = LoggerFactory.getLogger(ParserListener.class);

  private Deque<ParseItem> stack = new ArrayDeque<>();

  public ParserListener() {
    stack.push(new BlockStatement(true));
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
    if(stack.size() == 2) {
      // assume single expression script
      findMostRecentStatement().addStatement(new ExpressionStatement(popExpression()));
    }
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

  private ContainerStatement findMostRecentStatement() {
    Iterator<ParseItem> iter = stack.iterator();
    while(iter.hasNext()) {
      ParseItem p = iter.next();
      if(p instanceof ContainerStatement) {
        return (ContainerStatement)p;
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
    FunctionParameters params = (FunctionParameters)stack.pop();
    FunctionName name = (FunctionName)stack.pop();
    stack.push(new FunctionCall(name, params.getParameters(), null));
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
  }

  @Override
  public void exitFparams(FparamsContext ctx) {
    log.debug("exit fparams '{}'", ctx.getText());
    List<FunctionParameter> l = new ArrayList<>();
    popTerminal(")");
    for(;;) {
      ParseItem pi = stack.pop();
      if(pi instanceof Terminal) {
        String t = ((Terminal)pi).getText();
        if("(".equals(t)) {
          break;
        }
        if(!",".equals(t)) {
          throw new ScriptException("unexpected terminal '%s' in parameter list, %s".formatted(t, ctx.getText()));
        }
      } else if(pi instanceof FunctionParameter) {
        l.add((FunctionParameter)pi);
      } else {
        throw new ScriptException("unexpected stack item '%s' in parameter list, %s".formatted(pi, ctx.getText()));
      }
    }
    Collections.reverse(l);
    stack.push(new FunctionParameters(l));
  }

  @Override
  public void enterFparam(FparamContext ctx) {
    log.debug("enter fparam '{}'", ctx.getText());
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

  private Terminal popTerminalIfExists(String expected) {
    ParseItem p = stack.peek();
    if(p instanceof Terminal) {
      if(((Terminal) p).getText().equals(expected)) {
        return popTerminal();
      }
    }
    return null;
  }

  private void popSemi() {
    popTerminal(";");
  }

  private boolean nextTerminalIs(String text) {
    ParseItem p = stack.peek();
    if(p instanceof Terminal) {
      Terminal t = (Terminal)p;
      return t.getText().equals(text);
    } else {
      return false;
    }
  }

  private boolean nextItemIs(Class<?> cls) {
    ParseItem p = stack.peek();
    return p!=null?cls.equals(p.getClass()):false;
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

  private void printStack() {
    log.debug("stack '{}'", stack);
  }

  public Statement getEntryPoint() {
    return (Statement)stack.getLast();
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
      String literal = StringUtils.substring(s, 1, s.length()-1).intern();
      stack.push(new StringLiteral(literal));
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
    LinkedList<VarDef> vars = new LinkedList<>();
    popSemi();
    for(;;) {
      ParseItem pi1 = stack.pop();
      ParseItem pi2 = stack.pop();
      if(pi2 instanceof Terminal) {
        String t2 = ((Terminal)pi2).getText();
        if("var".equals(t2) || ",".equals(t2)) {
          Identifier i1 = (Identifier)pi1;
          vars.addFirst(new VarDef(i1.getIdent(), null));
          if("var".equals(t2)) {
            break;
          }
        } else if( "=".equals(t2)) {
          ParseItem pi3 = stack.pop();
          String t4 = popTerminal().getText();
          Identifier i3 = (Identifier)pi3;
          vars.addFirst(new VarDef(i3.getIdent(), (Expression)pi1));
          if("var".equals(t4)) {
            break;
          } else if(!",".equals(t4)) {
            throw new ScriptException("expected terminals var or , but got '%s'".formatted(t4));
          }
        } else {
          throw new ScriptException("expected 'var' or ',' or '=', but got '%s'".formatted(((Terminal)pi2).getText()));
        }
      } else {
        throw new SyntaxException("expected terminal in vardef but got '%s'".formatted(pi2));
      }
    }
    stack.push(new VardefStatement(vars));
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

  @Override
  public void enterCcall(CcallContext ctx) {
    log.debug("enterCcall '{}'", ctx.getText());
  }

  @Override
  public void exitCcall(CcallContext ctx) {
    log.debug("exitCcall '{}'", ctx.getText());
    FunctionParameters params = (FunctionParameters)stack.pop();
    TypeName name = (TypeName)stack.pop();
    // exitCname already popped the the 'new' terminal of the stack
    stack.push(new NewOp(name.getName(), params.getParameters()));
  }

  @Override
  public void enterCname(CnameContext ctx) {
    log.debug("enterCname '{}'", ctx.getText());
  }

  @Override
  public void exitCname(CnameContext ctx) {
    log.debug("exitCname '{}'", ctx.getText());
    LinkedList<String> l = new LinkedList<>();
    for(;;) {
      String t = popTerminal().getText();
      if("new".equals(t)) {
        break;
      }
      l.addFirst(t);
    }
    stack.push(new TypeName(l.stream().collect(Collectors.joining())));
  }

  @Override
  public void enterNullLiteral(NullLiteralContext ctx) {
    log.debug("enterNullLiteral '{}'", ctx.getText());
  }

  @Override
  public void exitNullLiteral(NullLiteralContext ctx) {
    log.debug("exitNullLiteral '{}'", ctx.getText());
    popTerminal("null");
    stack.push(new NullLiteral());
  }

  @Override
  public void enterWhileStmt(WhileStmtContext ctx) {
    log.debug("enterWhileStmt '{}'", ctx.getText());
    stack.push(new WhileStatement());
  }

  @Override
  public void exitWhileStmt(WhileStmtContext ctx) {
    log.debug("exitWhileStmt '{}'", ctx.getText());
    popTerminal(")");
    Expression e = popExpression();
    popTerminal("(");
    popTerminal("while");
    WhileStatement ws = (WhileStatement)findMostRecentStatement();
    ws.setExpression(e);
  }

  @Override
  public void enterForStmt(ForStmtContext ctx) {
    log.debug("enterForStmt '{}'", ctx.getText());
    stack.push(new ForStatementBuilder());
  }

  @Override
  public void exitForStmt(ForStmtContext ctx) {
    log.debug("exitForStmt '{}'", ctx.getText());
    printStack();
    popTerminal(")");
    ForStatementBuilder b = (ForStatementBuilder)stack.pop();
    var forStmt = new ForStatement(b.getForInit(), b.getForTerm(), b.getForInc(), b.getStatement());
    log.debug("adding '{}' to stack", forStmt);
    stack.push(forStmt);
  }

  @Override
  public void enterForInit(ForInitContext ctx) {
    log.debug("enterForInit '{}'", ctx.getText());
    // get rid of terminals so the ForStatementBuilder is the next
    popTerminal("(");
    popTerminal("for");
    if(!(stack.peek() instanceof ForStatementBuilder)) {
      throw new ScriptException("expected ForStatementBuilder");
    }
  }

  @Override
  public void exitForInit(ForInitContext ctx) {
    log.debug("exitForInit '{}'", ctx.getText());
    if(stack.peek() instanceof Terminal) {
      popTerminal(";");
      List<AssignmentOperator> l = new LinkedList<>();
      for(;stack.peek() instanceof AssignmentOperator;) {
        l.add((AssignmentOperator)stack.pop());
        popTerminalIfExists(",");
      }
      printStack();
      ((ForStatementBuilder)stack.peek()).setForInit(new ForInitStatement(l));
    } else {
      Statement s = popStatement();
      if(s instanceof VardefStatement) {
        ((ForStatementBuilder)stack.peek()).setForInit(new ForInitStatement((VardefStatement)s));
      } else if(!(s instanceof EmptyStatement)) {
        throw new ScriptException("expected var def or empty statement but got, " + s);
      }
    }
  }

  @Override
  public void enterForTerm(ForTermContext ctx) {
    log.debug("enterForTerm '{}'", ctx.getText());
  }

  @Override
  public void exitForTerm(ForTermContext ctx) {
    log.debug("exitForTerm '{}'", ctx.getText());
    popTerminal(";");
    if(stack.peek() instanceof Expression) {
      Expression expr = popExpression();
      ((ForStatementBuilder)stack.peek()).setForTerm(expr);
    }
  }

  @Override
  public void enterForInc(ForIncContext ctx) {
    log.debug("enterForInc '{}'", ctx.getText());
  }

  @Override
  public void exitForInc(ForIncContext ctx) {
    log.debug("exitForInc '{}'", ctx.getText());
    LinkedList<Expression> l = new LinkedList<>();
    for(;;) {
      if(stack.peek() instanceof ForStatementBuilder) {
        ((ForStatementBuilder)stack.peek()).setForInc(l);
        break;
      } else if(stack.peek() instanceof Expression) {
        l.addFirst(popExpression());
      } else if(stack.peek() instanceof Terminal) {
        popTerminal(",");
      } else {
        throw new ScriptException("unexpected item on top of stack, " + stack.peek());
      }
    }
  }

  @Override
  public void enterHeader(HeaderContext ctx) {
    log.debug("enterHeader '{}'", ctx.getText());
  }

  @Override
  public void exitHeader(HeaderContext ctx) {
    log.debug("exitHeader '{}'", ctx.getText());
  }

  @Override
  public void enterImportStmt(ImportStmtContext ctx) {
    log.debug("enterImportStmt '{}'", ctx.getText());
  }

  @Override
  public void exitImportStmt(ImportStmtContext ctx) {
    log.debug("exitImportStmt '{}'", ctx.getText());
    popSemi();
    ImportType type = (ImportType)stack.pop();
    if(nextTerminalIs("static")) {
      popTerminal("static");
      findMostRecentStatement().addStatement(new ImportStaticStatement(type.getType()));
    } else {
      findMostRecentStatement().addStatement(new ImportStatement(type.getType()));
    }
    popTerminal("import");
  }

  @Override
  public void enterImportType(ImportTypeContext ctx) {
    log.debug("enterImportType '{}'", ctx.getText());
    stack.push(new ImportTypeStartMarker());
  }

  @Override
  public void exitImportType(ImportTypeContext ctx) {
    log.debug("exitImportType '{}'", ctx.getText());
    String type = "";
    for(;;) {
      if(nextItemIs(ImportTypeStartMarker.class)) {
        stack.pop();
        break;
      }
      type = popTerminal().getText() + type;
    }
    stack.push(new ImportType(type));
  }

  @Override
  public void enterFunctionAlias(FunctionAliasContext ctx) {
    log.debug("enterFunctionAlias '{}'", ctx.getText());
  }

  @Override
  public void exitFunctionAlias(FunctionAliasContext ctx) {
    log.debug("exitFunctionAlias '{}'", ctx.getText());
    popSemi();
    DottedIdentifier dident = (DottedIdentifier)stack.pop();
    Identifier ident = popIdentifier();
    popTerminal("alias");
    findMostRecentStatement().addStatement(new FunctionAlias(ident.getIdent(), dident.getIdent()));
  }

  @Override
  public void enterDottedIdent(DottedIdentContext ctx) {
    log.debug("enterDottedIdent '{}'", ctx.getText());
    stack.push(new DottedIdentStartMarker());
  }

  @Override
  public void exitDottedIdent(DottedIdentContext ctx) {
    log.debug("exitDottedIdent '{}'", ctx.getText());
    for(;;) {
      if(nextItemIs(DottedIdentStartMarker.class)) {
        stack.pop();
        break;
      }
      stack.pop();
    }
    stack.push(new DottedIdentifier(ctx.getText()));
  }

}
