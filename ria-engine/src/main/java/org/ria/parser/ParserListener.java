/*
 * Copyright 2023 Andre Gebers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ria.parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;
import org.ria.ScriptException;
import org.ria.antlr.ScriptListener;
import org.ria.antlr.ScriptParser.ArrayInitContext;
import org.ria.antlr.ScriptParser.ArrowCaseContext;
import org.ria.antlr.ScriptParser.AssignContext;
import org.ria.antlr.ScriptParser.AssignmentContext;
import org.ria.antlr.ScriptParser.AssignmentOpContext;
import org.ria.antlr.ScriptParser.BlockContext;
import org.ria.antlr.ScriptParser.BoolLiteralContext;
import org.ria.antlr.ScriptParser.BreakStmtContext;
import org.ria.antlr.ScriptParser.CasesContext;
import org.ria.antlr.ScriptParser.CatchBlockContext;
import org.ria.antlr.ScriptParser.CcallContext;
import org.ria.antlr.ScriptParser.ColonCaseContext;
import org.ria.antlr.ScriptParser.ConstructorRefContext;
import org.ria.antlr.ScriptParser.ContinueStmtContext;
import org.ria.antlr.ScriptParser.DoWhileStmtContext;
import org.ria.antlr.ScriptParser.EmptyStmtContext;
import org.ria.antlr.ScriptParser.ExprContext;
import org.ria.antlr.ScriptParser.ExprStmtContext;
import org.ria.antlr.ScriptParser.FDefParamsContext;
import org.ria.antlr.ScriptParser.FcallContext;
import org.ria.antlr.ScriptParser.FinallyBlockContext;
import org.ria.antlr.ScriptParser.FloatLiteralContext;
import org.ria.antlr.ScriptParser.FnameContext;
import org.ria.antlr.ScriptParser.ForEachStmtContext;
import org.ria.antlr.ScriptParser.ForIncContext;
import org.ria.antlr.ScriptParser.ForInitContext;
import org.ria.antlr.ScriptParser.ForStmtContext;
import org.ria.antlr.ScriptParser.ForTermContext;
import org.ria.antlr.ScriptParser.FparamContext;
import org.ria.antlr.ScriptParser.FparamsContext;
import org.ria.antlr.ScriptParser.FunctionDefinitionContext;
import org.ria.antlr.ScriptParser.HeaderContext;
import org.ria.antlr.ScriptParser.HeaderElementContext;
import org.ria.antlr.ScriptParser.IdentContext;
import org.ria.antlr.ScriptParser.IfElseStmtContext;
import org.ria.antlr.ScriptParser.IfStmtContext;
import org.ria.antlr.ScriptParser.ImportStmtContext;
import org.ria.antlr.ScriptParser.ImportTypeContext;
import org.ria.antlr.ScriptParser.IntLiteralContext;
import org.ria.antlr.ScriptParser.JavaTypeDefContext;
import org.ria.antlr.ScriptParser.LambdaContext;
import org.ria.antlr.ScriptParser.LiteralContext;
import org.ria.antlr.ScriptParser.MethodRefContext;
import org.ria.antlr.ScriptParser.MultiAssignmentOpContext;
import org.ria.antlr.ScriptParser.NewArrayContext;
import org.ria.antlr.ScriptParser.NewArrayInitContext;
import org.ria.antlr.ScriptParser.NullLiteralContext;
import org.ria.antlr.ScriptParser.ObjectScopeStmtContext;
import org.ria.antlr.ScriptParser.ReturnStmtContext;
import org.ria.antlr.ScriptParser.ScriptContext;
import org.ria.antlr.ScriptParser.StmtContext;
import org.ria.antlr.ScriptParser.StrLiteralContext;
import org.ria.antlr.ScriptParser.SwitchExprContext;
import org.ria.antlr.ScriptParser.SwitchStmtContext;
import org.ria.antlr.ScriptParser.ThrowStmtContext;
import org.ria.antlr.ScriptParser.TryResourceContext;
import org.ria.antlr.ScriptParser.TryStmtContext;
import org.ria.antlr.ScriptParser.TypeContext;
import org.ria.antlr.ScriptParser.TypeOrPrimitiveContext;
import org.ria.antlr.ScriptParser.TypeOrPrimitiveOrVarContext;
import org.ria.antlr.ScriptParser.VardefStmtContext;
import org.ria.antlr.ScriptParser.VoidLiteralContext;
import org.ria.antlr.ScriptParser.WhileStmtContext;
import org.ria.antlr.ScriptParser.YieldStmtContext;
import org.ria.expression.Assignment;
import org.ria.expression.AssignmentOp;
import org.ria.expression.BoolLiteral;
import org.ria.expression.ConstructorReference;
import org.ria.expression.Expression;
import org.ria.expression.FloatLiteral;
import org.ria.expression.FunctionCall;
import org.ria.expression.Ident;
import org.ria.expression.Identifier;
import org.ria.expression.IntLiteral;
import org.ria.expression.MethodReference;
import org.ria.expression.MultiAssignmentOp;
import org.ria.expression.NewArrayInitOp;
import org.ria.expression.NewArrayOp;
import org.ria.expression.NewOp;
import org.ria.expression.NullLiteral;
import org.ria.expression.ObjectScopeExpression;
import org.ria.expression.StringLiteral;
import org.ria.expression.SwitchArrowCase;
import org.ria.expression.SwitchColonCase;
import org.ria.expression.SwitchExpression;
import org.ria.expression.Type;
import org.ria.expression.VoidLiteral;
import org.ria.java.JavaTypeSource;
import org.ria.statement.BlockStatement;
import org.ria.statement.BreakStatement;
import org.ria.statement.CatchBlock;
import org.ria.statement.ContainerStatement;
import org.ria.statement.ContinueStatement;
import org.ria.statement.DoWhileStatement;
import org.ria.statement.EmptyStatement;
import org.ria.statement.ExpressionStatement;
import org.ria.statement.FinallyBlock;
import org.ria.statement.ForEachStatement;
import org.ria.statement.ForInitStatement;
import org.ria.statement.ForStatementBuilder;
import org.ria.statement.Function;
import org.ria.statement.HeaderEnterStatement;
import org.ria.statement.HeaderExitStatement;
import org.ria.statement.IfStatement;
import org.ria.statement.ImportStatement;
import org.ria.statement.ImportStaticStatement;
import org.ria.statement.ReturnStatement;
import org.ria.statement.Statement;
import org.ria.statement.ThrowStatement;
import org.ria.statement.TryResource;
import org.ria.statement.TryStatement;
import org.ria.statement.VarDef;
import org.ria.statement.VardefStatement;
import org.ria.statement.WhileStatement;
import org.ria.statement.YieldStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParserListener implements ScriptListener {

  private static final Logger log = LoggerFactory.getLogger(ParserListener.class);

  private Deque<ParseItem> stack = new ArrayDeque<>();

  private HeaderEnterStatement headerEnter;

  private HeaderExitStatement headerExit;

  public ParserListener(
      HeaderEnterStatement headerEnter,
      HeaderExitStatement headerExit) {
    this.headerEnter = headerEnter;
    this.headerExit = headerExit;
    // add main function
    Function main = Function.main();
    stack.push(main);
    // add main function body
    stack.push(main.getStatements());
  }

  @Override
  public void visitTerminal(TerminalNode node) {
    log.trace("visit terminal '{}'", node.getSymbol().getText());
    if(ReservedKeywords.isReservedKeyword(node.getSymbol().getText())) {
      throw new ReservedKeywordException("reserved keyword '%s' on line '%s'"
          .formatted(node.getSymbol().getText(), node.getSymbol().getLine()));
    } else {
      stack.push(new Terminal(node.getSymbol()));
    }
  }

  @Override
  public void visitErrorNode(ErrorNode node) {
    log.trace("visit error node '{}'", node);
  }

  @Override
  public void enterEveryRule(ParserRuleContext ctx) {
//    log.debug("enterEveryRule '{}'", ctx.getText());
  }

  @Override
  public void exitEveryRule(ParserRuleContext ctx) {
//    log.debug("exitEveryRule '{}'", ctx.getText());
  }

  @Override
  public void enterScript(ScriptContext ctx) {
    log.debug("enter script");
  }

  @Override
  public void exitScript(ScriptContext ctx) {
    log.debug("stack size '{}'", stack.size());
    if(stack.size() == 3) {
      // assume single expression script
      // FIXME line number should be of the expression
      findMostRecentContainerStatement().addStatement(new ExpressionStatement(0, popExpression()));
    }
    if(stack.size() != 2) {
      log.warn("stack should have single item but has '{}', '{}'", stack.size(), stack);
    }
    log.debug("parse done");
  }

  @Override
  public void enterStmt(StmtContext ctx) {
    log.trace("enterStmt '{}'", ctx.getText());
  }

  @Override
  public void exitStmt(StmtContext ctx) {
    log.trace("exitStmt '{}'", ctx.getText());
    log.trace("'{}'", stack);
    Statement stmt = popStatement();
    if(stmt instanceof Function) {
      findMostRecentFunction().addFunction((Function)stmt);
    } else {
      findMostRecentContainerStatement().addStatement(stmt);
    }
  }

  private <T> T findMostRecent(Class<T> cls) {
    ParseItem pi = stack.stream()
        .filter(item -> cls.isAssignableFrom(item.getClass()))
        .findFirst()
        .orElse(null);
    return cls.cast(pi);
  }

  private ContainerStatement findMostRecentContainerStatement() {
    return findMostRecent(ContainerStatement.class);
  }

  private Function findMostRecentFunction() {
    return findMostRecent(Function.class);
  }

  @Override
  public void enterReturnStmt(ReturnStmtContext ctx) {
    log.trace("enter return stmt '{}'", ctx.getText());
  }

  @Override
  public void exitReturnStmt(ReturnStmtContext ctx) {
    log.trace("exit return stmt '{}'", ctx.getText());
    popSemi();
    Expression expr = null;
    if(stack.getFirst() instanceof Expression) {
      expr = popExpression();
    }
    popTerminal("return");
    stack.push(new ReturnStatement(ctx.getStart().getLine(), expr));
  }

  @Override
  public void enterAssignment(AssignmentContext ctx) {
    log.trace("enter assign '{}'", ctx.getText());
  }

  @Override
  public void exitAssignment(AssignmentContext ctx) {
    log.trace("exit assign '{}'", ctx.getText());
  }

  private List<ParseTree> getChildren(ParserRuleContext ctx) {
    ArrayList<ParseTree> l = new ArrayList<>();
    for(int i=0;i<ctx.getChildCount();i++) {
      l.add(ctx.getChild(i));
    }
    return l;
  }

  private boolean isTerminal(ParseTree parseTree, String s) {
    return parseTree instanceof TerminalNode n && n.getText().equals(s);
  }

  private boolean isObjectScopeExpression(ParserRuleContext ctx) {
    List<ParseTree> children = getChildren(ctx);
    return
        (children.size() >= 3) &&
        (children.get(0) instanceof ExprContext) &&
        isTerminal(children.get(1), "{") &&
        isTerminal(children.get(children.size()-1), "}");
  }

  private boolean isObjectScopeExpressionWithStatements(ParserRuleContext ctx) {
    return
        isObjectScopeExpression(ctx) &&
        (ctx.getChildCount() > 3) &&
        (ctx.getChild(2) instanceof StmtContext);
  }

  private ObjectScopeExpression parseObjectScopeExpression(ParserRuleContext ctx) {
    popTerminal("}");
    LinkedList<Expression> l = new LinkedList<>();
    for(;;) {
      if(nextItemIsExpression()) {
        l.addFirst(popExpression());
      } else if(nextTerminalIs("{")) {
        popTerminal("{");
        break;
      } else {
        throw new ScriptException("unexpected item on stack '%s'".formatted(stack.peek()));
      }
    }
    Expression expression = popExpression();
    BlockStatement block = null;
    if(isObjectScopeExpressionWithStatements(ctx)) {
      block = pop(BlockStatement.class);
    }
    return new ObjectScopeExpression(expression, l, block);
  }

  @Override
  public void enterExpr(ExprContext ctx) {
    log.trace("enter expr '{}'", ctx.getText());
    if(isObjectScopeExpression(ctx)) {
      if(isObjectScopeExpressionWithStatements(ctx)) {
        stack.push(new BlockStatement(ctx.getStart().getLine()));
      }
    } else {
      // push an expression start marker on the stack
      // so we know how far to go back on exitExpr
      stack.push(new ExpressionStartMarker(ctx));
    }
  }

  @Override
  public void exitExpr(ExprContext ctx) {
    log.trace("exit expr '{}'", ctx.getText());
    if(isObjectScopeExpression(ctx)) {
      stack.push(parseObjectScopeExpression(ctx));
    } else {
      new ExpressionParser(ctx, stack).parse();
    }
  }

  @Override
  public void enterFcall(FcallContext ctx) {
    log.trace("enter fcall '{}'", ctx.getText());
  }

  @Override
  public void exitFcall(FcallContext ctx) {
    log.trace("exit fcall '{}'", ctx.getText());
    FunctionParameters params = (FunctionParameters)stack.pop();
    FunctionName name = (FunctionName)stack.pop();
    stack.push(new FunctionCall(name, params.getParameters(), null));
  }

  @Override
  public void enterFname(FnameContext ctx) {
    log.trace("enter fname '{}'", ctx.getText());
  }

  @Override
  public void exitFname(FnameContext ctx) {
    log.trace("exit fname '{}'", ctx.getText());
    stack.push(new FunctionName(popTerminal().getToken().getText()));
  }

  @Override
  public void enterFparams(FparamsContext ctx) {
    log.trace("enter fparams '{}'", ctx.getText());
  }

  @Override
  public void exitFparams(FparamsContext ctx) {
    log.trace("exit fparams '{}'", ctx.getText());
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
    log.trace("enter fparam '{}'", ctx.getText());
  }

  @Override
  public void exitFparam(FparamContext ctx) {
    log.trace("exit fparam '{}'", ctx.getText());
    stack.push(new FunctionParameter((Expression)stack.pop()));
  }

  @Override
  public void enterLiteral(LiteralContext ctx) {
    log.trace("enter literal '{}'", ctx.getText());
  }

  @Override
  public void exitLiteral(LiteralContext ctx) {
    log.trace("exit literal '{}'", ctx.getText());
  }

  @Override
  public void enterIdent(IdentContext ctx) {
    log.trace("enter ident '{}'", ctx.getText());
  }

  @Override
  public void exitIdent(IdentContext ctx) {
    log.trace("exit ident '{}'", ctx.getText());
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

  private <T> T pop(Class<T> cls) {
    return cls.cast(stack.pop());
  }

  private <T> T popIfExists(Class<T> cls) {
    return nextItemIs(cls)?pop(cls):null;
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
    return p!=null?cls.isAssignableFrom(p.getClass()):false;
  }

  private boolean nextItemIsExpression() {
    return nextItemIs(Expression.class);
  }

  private boolean nextItemIsIdentifier() {
    return nextItemIs(Identifier.class);
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
    log.trace("stack '{}'", stack);
  }

  public Function getMainFunction() {
    return (Function)stack.getLast();
  }

  @Override
  public void enterStrLiteral(StrLiteralContext ctx) {
    log.trace("enter strLiteral '{}'", ctx.getText());
  }

  @Override
  public void exitStrLiteral(StrLiteralContext ctx) {
    log.trace("exit strLiteral '{}'", ctx.getText());
    Terminal t = (Terminal)stack.pop();
    String s = t.getToken().getText();
    if(TextBlockUtil.isTextBlock(s)) {
      stack.push(new StringLiteral(TextBlockUtil.toString(s)));
    } else if(StringUtils.startsWith(s, "\"") && StringUtils.endsWith(s, "\"")) {
      if(s.length() == 1) {
        fail("invalid string literal " + s);
      }
      String literal = StringUtils.substring(s, 1, s.length()-1).intern();
      stack.push(new StringLiteral(literal));
    } else if(StringUtils.startsWith(s, "'") && StringUtils.endsWith(s, "'")) {
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
    log.trace("enter boolLiteral '{}'", ctx.getText());
  }

  @Override
  public void exitBoolLiteral(BoolLiteralContext ctx) {
    log.trace("exit boolLiteral '{}'", ctx.getText());
    stack.push(new BoolLiteral(popTerminal().getText()));
  }

  @Override
  public void enterFloatLiteral(FloatLiteralContext ctx) {
    log.trace("enter floatLiteral '{}'", ctx.getText());
  }

  @Override
  public void exitFloatLiteral(FloatLiteralContext ctx) {
    log.trace("exit floatLiteral '{}'", ctx.getText());
    stack.push(new FloatLiteral(popTerminal().getText()));
  }

  @Override
  public void enterIntLiteral(IntLiteralContext ctx) {
    log.trace("enter intLiteral '{}'", ctx.getText());
  }

  @Override
  public void exitIntLiteral(IntLiteralContext ctx) {
    log.trace("exit intLiteral '{}'", ctx.getText());
    stack.push(new IntLiteral(popTerminal().getText()));
  }

  @Override
  public void enterAssignmentOp(AssignmentOpContext ctx) {
    log.trace("enter AssignmentOp '{}'", ctx.getText());
  }

  @Override
  public void exitAssignmentOp(AssignmentOpContext ctx) {
    log.trace("exit AssignmentOp '{}'", ctx.getText());
    Expression expr = popExpression();
    popTerminal("=");
    Identifier ident = popIdentifier();
    stack.push(new AssignmentOp(ident, expr));
  }

  @Override
  public void enterEmptyStmt(EmptyStmtContext ctx) {
    log.trace("enterEmptyStmt '{}'", ctx.getText());
  }

  @Override
  public void exitEmptyStmt(EmptyStmtContext ctx) {
    log.trace("exitEmptyStmt '{}'", ctx.getText());
    popSemi();
    stack.push(new EmptyStatement(ctx.getStart().getLine()));
  }

  @Override
  public void enterExprStmt(ExprStmtContext ctx) {
    log.trace("enterExprStmt '{}'", ctx.getText());
  }

  @Override
  public void exitExprStmt(ExprStmtContext ctx) {
    log.trace("exitExprStmt '{}'", ctx.getText());
    popSemi();
    stack.push(new ExpressionStatement(ctx.getStart().getLine(), popExpression()));
  }

  @Override
  public void enterVardefStmt(VardefStmtContext ctx) {
    log.trace("enterVardefStmt '{}'", ctx.getText());
  }

  @Override
  public void exitVardefStmt(VardefStmtContext ctx) {
    log.trace("exitVardefStmt '{}'", ctx.getText());
    LinkedList<VarDef> vars = new LinkedList<>();
    org.ria.parser.Type type;
    popSemi();
    for(;;) {
      if(nextItemIs(TypeOrPrimitive.class)) {
        TypeOrPrimitive tp = pop(TypeOrPrimitive.class);
        // if the variable was declared with 'var' then tp.getType() returns null
        type = tp.getType();
        break;
      } else if(nextTerminalIs(",")) {
        popTerminal();
      } else {
        ParseItem pi = stack.pop();
        if(pi instanceof Assignment assign) {
          vars.addFirst(new VarDef(assign));
        } else if(pi instanceof Identifier ident) {
          vars.addFirst(new VarDef(ident));
        } else {
          throw new ScriptException("unexpected stack item '%s'".formatted(pi));
        }
      }
    }
    stack.push(new VardefStatement(ctx.getStart().getLine(), vars, type));
  }

  @Override
  public void enterBlock(BlockContext ctx) {
    log.trace("enterBlock '{}'", ctx.getText());
    stack.push(new BlockStatement(ctx.getStart().getLine()));
  }

  @Override
  public void exitBlock(BlockContext ctx) {
    log.trace("exitBlock '{}'", ctx.getText());
    log.trace("stack '{}'", stack);
    popTerminal("}");
    popTerminal("{");
  }

  @Override
  public void enterIfStmt(IfStmtContext ctx) {
    log.trace("enterIfStmt '{}'", ctx.getText());
    stack.push(new IfStatement(ctx.getStop().getLine()));
  }

  @Override
  public void exitIfStmt(IfStmtContext ctx) {
    log.trace("exitIfStmt '{}'", ctx.getText());
    log.trace("stack '{}'", stack);
    popTerminal(")");
    Expression e = popExpression();
    popTerminal("(");
    popTerminal("if");
    IfStatement s = (IfStatement)stack.getFirst();
    s.setExpression(e);
  }

  @Override
  public void enterIfElseStmt(IfElseStmtContext ctx) {
    log.trace("enterIfElseStmt '{}'", ctx.getText());
    stack.push(new IfStatement(ctx.getStart().getLine()));
  }

  @Override
  public void exitIfElseStmt(IfElseStmtContext ctx) {
    log.trace("exitIfElseStmt '{}'", ctx.getText());
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
    log.trace("enterCcall '{}'", ctx.getText());
  }

  @Override
  public void exitCcall(CcallContext ctx) {
    log.trace("exitCcall '{}'", ctx.getText());
    FunctionParameters params = (FunctionParameters)stack.pop();
    Type type = (Type)stack.pop();
    popTerminal("new");
    stack.push(new NewOp(type.getIdent(), params.getParameters()));
  }

  @Override
  public void enterNullLiteral(NullLiteralContext ctx) {
    log.trace("enterNullLiteral '{}'", ctx.getText());
  }

  @Override
  public void exitNullLiteral(NullLiteralContext ctx) {
    log.trace("exitNullLiteral '{}'", ctx.getText());
    popTerminal("null");
    stack.push(new NullLiteral());
  }

  @Override
  public void enterWhileStmt(WhileStmtContext ctx) {
    log.trace("enterWhileStmt '{}'", ctx.getText());
    stack.push(new WhileStatement(ctx.getStart().getLine()));
  }

  @Override
  public void exitWhileStmt(WhileStmtContext ctx) {
    log.trace("exitWhileStmt '{}'", ctx.getText());
    popTerminal(")");
    Expression e = popExpression();
    popTerminal("(");
    popTerminal("while");
    WhileStatement ws = (WhileStatement)findMostRecentContainerStatement();
    ws.setExpression(e);
  }

  @Override
  public void enterForStmt(ForStmtContext ctx) {
    log.trace("enterForStmt '{}'", ctx.getText());
    stack.push(new ForStatementBuilder(ctx.getStart().getLine()));
  }

  @Override
  public void exitForStmt(ForStmtContext ctx) {
    log.trace("exitForStmt '{}'", ctx.getText());
    printStack();
    popTerminal(")");
    ForStatementBuilder b = (ForStatementBuilder)stack.pop();
    var forStmt = b.create();
    log.trace("adding '{}' to stack", forStmt);
    stack.push(forStmt);
  }

  @Override
  public void enterForInit(ForInitContext ctx) {
    log.trace("enterForInit '{}'", ctx.getText());
    // get rid of terminals so the ForStatementBuilder is the next
    popTerminal("(");
    popTerminal("for");
    if(!(stack.peek() instanceof ForStatementBuilder)) {
      throw new ScriptException("expected ForStatementBuilder");
    }
  }

  @Override
  public void exitForInit(ForInitContext ctx) {
    log.trace("exitForInit '{}'", ctx.getText());
    if(stack.peek() instanceof Terminal) {
      popTerminal(";");
      List<Assignment> l = new LinkedList<>();
      for(;stack.peek() instanceof Assignment;) {
        l.add((Assignment)stack.pop());
        popTerminalIfExists(",");
      }
      printStack();
      ((ForStatementBuilder)stack.peek()).setForInit(
          new ForInitStatement(ctx.getStart().getLine(),l));
    } else {
      Statement s = popStatement();
      if(s instanceof VardefStatement) {
        ((ForStatementBuilder)stack.peek()).setForInit(
            new ForInitStatement(ctx.getStart().getLine(), (VardefStatement)s));
      } else if(!(s instanceof EmptyStatement)) {
        throw new ScriptException("expected var def or empty statement but got, " + s);
      }
    }
  }

  @Override
  public void enterForTerm(ForTermContext ctx) {
    log.trace("enterForTerm '{}'", ctx.getText());
  }

  @Override
  public void exitForTerm(ForTermContext ctx) {
    log.trace("exitForTerm '{}'", ctx.getText());
    popTerminal(";");
    if(stack.peek() instanceof Expression) {
      Expression expr = popExpression();
      ((ForStatementBuilder)stack.peek()).setForTerm(expr);
    }
  }

  @Override
  public void enterForInc(ForIncContext ctx) {
    log.trace("enterForInc '{}'", ctx.getText());
  }

  @Override
  public void exitForInc(ForIncContext ctx) {
    log.trace("exitForInc '{}'", ctx.getText());
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
    log.trace("enterHeader '{}'", ctx.getText());
    findMostRecentContainerStatement().addStatement(headerEnter);
  }

  @Override
  public void exitHeader(HeaderContext ctx) {
    log.trace("exitHeader '{}'", ctx.getText());
    findMostRecentContainerStatement().addStatement(headerExit);
  }

  @Override
  public void enterImportStmt(ImportStmtContext ctx) {
    log.trace("enterImportStmt '{}'", ctx.getText());
  }

  @Override
  public void exitImportStmt(ImportStmtContext ctx) {
    log.trace("exitImportStmt '{}'", ctx.getText());
    popSemi();
    ImportType type = null;
    if(nextItemIs(ImportType.class)) {
      type = pop(ImportType.class);
    } else if(nextItemIs(StringLiteral.class)) {
      StringLiteral s = pop(StringLiteral.class);
      type = new ImportType(s.getUnescaped());
    } else {
      throw new ScriptException("unexpected item on stack '%s'".formatted(Objects.toString(stack.peek())));
    }
    Statement stmt = null;
    if(nextTerminalIs("static")) {
      popTerminal("static");
      stmt = new ImportStaticStatement(ctx.getStart().getLine(), type.getType());
    } else {
      stmt = new ImportStatement(ctx.getStart().getLine(), type.getType());
    }
    popTerminal("import");
    stack.push(stmt);
  }

  @Override
  public void enterImportType(ImportTypeContext ctx) {
    log.trace("enterImportType '{}'", ctx.getText());
    stack.push(new ImportTypeStartMarker());
  }

  @Override
  public void exitImportType(ImportTypeContext ctx) {
    log.trace("exitImportType '{}'", ctx.getText());
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
  public void enterFunctionDefinition(FunctionDefinitionContext ctx) {
    log.trace("enterFunctionDefinition '{}'", ctx.getText());
    stack.push(new Function(ctx.getStart().getLine()));
  }

  @Override
  public void exitFunctionDefinition(FunctionDefinitionContext ctx) {
    log.trace("exitFunctionDefinition '{}'", ctx.getText());
    BlockStatement block = (BlockStatement)stack.pop();
    FunctionParameterIdentifiers params = (FunctionParameterIdentifiers)stack.pop();
    FunctionName name = (FunctionName)stack.pop();
    popTerminal("function");
    Function function = (Function)stack.peek();
    function.setName(name.getName());
    function.setParameterNames(params.getIdentifiers()
        .stream()
        .map(ident -> ident.getIdent())
        .toList());
    function.setStatements(block);
  }

  @Override
  public void enterBreakStmt(BreakStmtContext ctx) {
    log.trace("enterBreakStmt '{}'", ctx.getText());
  }

  @Override
  public void exitBreakStmt(BreakStmtContext ctx) {
    log.trace("exitBreakStmt '{}'", ctx.getText());
    popSemi();
    popTerminal("break");
    stack.push(new BreakStatement(ctx.getStart().getLine()));
  }

  @Override
  public void enterContinueStmt(ContinueStmtContext ctx) {
    log.trace("enterContinueStmt '{}'", ctx.getText());
  }

  @Override
  public void exitContinueStmt(ContinueStmtContext ctx) {
    log.trace("exitContinueStmt '{}'", ctx.getText());
    popSemi();
    popTerminal("continue");
    stack.push(new ContinueStatement(ctx.getStart().getLine()));
  }

  @Override
  public void enterDoWhileStmt(DoWhileStmtContext ctx) {
    log.trace("enterDoWhileStmt '{}'", ctx.getText());
    stack.push(new DoWhileStatement(ctx.getStart().getLine()));
  }

  @Override
  public void exitDoWhileStmt(DoWhileStmtContext ctx) {
    log.trace("exitDoWhileStmt '{}'", ctx.getText());
    popSemi();
    popTerminal(")");
    Expression e = popExpression();
    popTerminal("(");
    popTerminal("while");
    popTerminal("do");
    DoWhileStatement ws = (DoWhileStatement)findMostRecentContainerStatement();
    ws.setExpression(e);
  }

//  @Override
//  public void enterCharLiteral(CharLiteralContext ctx) {
//    log.trace("enterCharLiteral '{}'", ctx.getText());
//  }
//
//  @Override
//  public void exitCharLiteral(CharLiteralContext ctx) {
//    log.trace("exitCharLiteral '{}'", ctx.getText());
//    Terminal t = popTerminal();
//    String s = t.getToken().getText();
//    if(StringUtils.startsWith(s, "'") && StringUtils.endsWith(s, "'")) {
//      stack.push(new CharLiteral(StringUtils.strip(s,"'")));
//    } else {
//      fail("char literal not enclosed with single quotes " + s);
//    }
//  }

  @Override
  public void enterForEachStmt(ForEachStmtContext ctx) {
    log.trace("enterForEachStmt '{}'", ctx.getText());
    stack.push(new ForEachStatement(ctx.getStart().getLine()));
  }

  @Override
  public void exitForEachStmt(ForEachStmtContext ctx) {
    log.trace("exitForEachStmt '{}'", ctx.getText());
    popTerminal(")");
    Expression expr = popExpression();
    popTerminal(":");
    Identifier ident = popIdentifier();
    TypeOrPrimitive type = popIfExists(TypeOrPrimitive.class);
    popTerminal("(");
    popTerminal("for");
    ForEachStatement forEach = (ForEachStatement)stack.peek();
    forEach.setIdentifier(ident.getIdent());
    forEach.setType(type!=null?type.getType():null);
    forEach.setIterable(expr);
  }

  @Override
  public void enterMultiAssignmentOp(MultiAssignmentOpContext ctx) {
    log.trace("enterMultiAssignmentOp '{}'", ctx.getText());
  }

  @Override
  public void exitMultiAssignmentOp(MultiAssignmentOpContext ctx) {
    log.trace("exitMultiAssignmentOp '{}'", ctx.getText());
    Expression expr = popExpression();
    popTerminal("=");
    popTerminal(")");
    List<Identifier> l = new ArrayList<>();
    for(;;) {
      l.add(popIdentifier());
      if(nextTerminalIs(",")) {
        popTerminal(",");
      } else if(nextTerminalIs("(")) {
        popTerminal("(");
        break;
      } else {
        throw new ScriptException("unexpected stack element '%s'".formatted(stack.peek()));
      }
    }
    Collections.reverse(l);
    stack.push(new MultiAssignmentOp(l, expr));
  }

  @Override
  public void enterAssign(AssignContext ctx) {
    log.trace("enterAssign '{}'", ctx.getText());
  }

  @Override
  public void exitAssign(AssignContext ctx) {
    log.trace("exitAssign '{}'", ctx.getText());
  }

  @Override
  public void enterLambda(LambdaContext ctx) {
    log.trace("enterLambda '{}'", ctx.getText());
    // push a function so nested function (inside the lambda are added to the lambda)
    stack.push(new Function(ctx.getStart().getLine()));
    // push a block as statements are automatically added to outer blocks (and not left on the stack)
    // if the lambda defines a block body it is nested inside this block but does it matter?
    // function definitions do not need this because the block is mandated through the grammar.
    // for lambdas any statement or expression is fine as lambda body
    stack.push(new BlockStatement(ctx.getStart().getLine()));
  }

  @Override
  public void exitLambda(LambdaContext ctx) {
    log.trace("exitLambda '{}'", ctx.getText());
    Expression expr = null;
    if(nextItemIsExpression()) {
      expr = popExpression();
    }
    // if the lambda body was a statement (including a block)
    // it was already added to the block pushed in enterLambda
    popTerminal("->");
    FunctionParameterIdentifiers params = null;
    if(nextItemIsIdentifier()) {
      Identifier ident = popIdentifier();
      params = new FunctionParameterIdentifiers(List.of(ident));
    } else if(nextItemIs(FunctionParameterIdentifiers.class)) {
      params = (FunctionParameterIdentifiers)stack.pop();
    } else {
      fail("expected single identifier or function parameter definition but next stack item is '%s'"
          .formatted(stack.peek()));
    }
    BlockStatement block = (BlockStatement)stack.pop();
    if(expr != null) {
      // TODO the ExpressionStatement should have the line number of the expression
      block.addStatement(new ExpressionStatement(ctx.getStart().getLine(), expr));
    }
    // pop the lambda of the stack first so findMostRecentFunction finds the parent function
    Function lambda = (Function)stack.pop();
    lambda.setStatements(block);
    lambda.setParameterNames(params.getIdentifiers()
        .stream()
        .map(Identifier::getIdent)
        .toList());
    lambda.setParent(findMostRecentFunction());
    lambda.setName("lambda");
    // put the lambda function back on the stack so it can be used as an expression
    stack.push(lambda);
  }

  @Override
  public void enterFDefParams(FDefParamsContext ctx) {
    log.trace("enterFDefParams '{}'", ctx.getText());
  }

  @Override
  public void exitFDefParams(FDefParamsContext ctx) {
    log.trace("exitFDefParams '{}'", ctx.getText());
    List<Identifier> l = new ArrayList<>();
    popTerminal(")");
    for(;;) {
      ParseItem pi = stack.pop();
      if(pi instanceof Terminal term) {
        String t = term.getText();
        if("(".equals(t)) {
          break;
        }
        if(!",".equals(t)) {
          throw new ScriptException("unexpected terminal '%s' in parameter list, %s".formatted(t, ctx.getText()));
        }
      } else if(pi instanceof Identifier ident) {
        l.add(ident);
      } else {
        throw new ScriptException("unexpected stack item '%s' in parameter list, %s".formatted(pi, ctx.getText()));
      }
    }
    Collections.reverse(l);
    stack.push(new FunctionParameterIdentifiers(l));
  }

  @Override
  public void enterMethodRef(MethodRefContext ctx) {
    log.trace("enterMethodRef '{}'", ctx.getText());
  }

  @Override
  public void exitMethodRef(MethodRefContext ctx) {
    log.trace("exitMethodRef '{}'", ctx.getText());
    String methodName = popIdentifier().getIdent();
    popTerminal("::");
    String varOrType = ((Ident)stack.pop()).getIdent();
    stack.push(new MethodReference(varOrType, methodName));
  }

  @Override
  public void enterConstructorRef(ConstructorRefContext ctx) {
    log.trace("enterConstructorRef '{}'", ctx.getText());
  }

  @Override
  public void exitConstructorRef(ConstructorRefContext ctx) {
    log.trace("exitConstructorRef '{}'", ctx.getText());
    popTerminal("new");
    popTerminal("::");
    int dim = 0;
    for(;;) {
      if(nextTerminalIs("]")) {
        popTerminal("]");
        popTerminal("[");
        dim++;
      } else {
        break;
      }
    }
    String type = ((Ident)stack.pop()).getIdent();
    stack.push(new ConstructorReference(new org.ria.parser.Type(type, dim)));
  }

  @Override
  public void enterType(TypeContext ctx) {
    log.trace("enterType '{}'", ctx.getText());
    stack.push(new StartMarker());
  }

  @Override
  public void exitType(TypeContext ctx) {
    log.trace("exitType '{}'", ctx.getText());
    for(;;) {
      if(nextItemIs(StartMarker.class)) {
        stack.pop();
        break;
      }
      stack.pop();
    }
    stack.push(new Type(ctx.getText()));
  }

  @Override
  public void enterNewArray(NewArrayContext ctx) {
    log.trace("enterNewArray '{}'", ctx.getText());
  }

  @Override
  public void exitNewArray(NewArrayContext ctx) {
    log.trace("exitNewArray '{}'", ctx.getText());
    LinkedList<Expression> dims = new LinkedList<>();
    for(;;) {
      if(nextTerminalIs("]")) {
        popTerminal("]");
        if(nextItemIsExpression()) {
          dims.addFirst(popExpression());
        } else {
          dims.addFirst(null);
        }
        popTerminal("[");
      } else if(nextItemIs(TypeOrPrimitive.class)) {
        break;
      } else {
        fail("unexpected stack item " + stack.peek());
      }
    }
    TypeOrPrimitive type = pop(TypeOrPrimitive.class);
    if(type.getType().getDim() > 0) {
      throw new ScriptException("first dimension required");
    }
    popTerminal("new");
    NewArrayOp.checkDimensions(dims);
    stack.push(new NewArrayOp(type.getType(), dims));
  }

  @Override
  public void enterNewArrayInit(NewArrayInitContext ctx) {
    log.trace("enterNewArrayInit '{}'", ctx.getText());
  }

  @Override
  public void exitNewArrayInit(NewArrayInitContext ctx) {
    log.trace("exitNewArrayInit '{}'", ctx.getText());
    ArrayInit init = pop(ArrayInit.class);
    TypeOrPrimitive type = pop(TypeOrPrimitive.class);
    popTerminal("new");
    stack.push(new NewArrayInitOp(type.getType(), init));
  }

  @Override
  public void enterTypeOrPrimitive(TypeOrPrimitiveContext ctx) {
    log.trace("enterTypeOrPrimitive '{}'", ctx.getText());
  }

  @Override
  public void exitTypeOrPrimitive(TypeOrPrimitiveContext ctx) {
    log.trace("exitTypeOrPrimitive '{}'", ctx.getText());
    int arrayDimensions = 0;
    for(;;) {
      if(nextTerminalIs("]")) {
        popTerminal("]");
        popTerminal("[");
        arrayDimensions++;
      } else {
        break;
      }
    }
    log.trace("array dimensions '{}'", arrayDimensions);
    if(nextItemIs(Terminal.class)) {
      Terminal type = popTerminal();
      stack.push(new TypeOrPrimitive(new org.ria.parser.Type(type.getText(), arrayDimensions)));
    } else if(nextItemIs(Type.class)) {
      Type type = pop(Type.class);
      stack.push(new TypeOrPrimitive(new org.ria.parser.Type(type.getIdent(), arrayDimensions)));
    }
  }

  @Override
  public void enterArrayInit(ArrayInitContext ctx) {
    log.trace("enterArrayInit '{}'", ctx.getText());
  }

  @Override
  public void exitArrayInit(ArrayInitContext ctx) {
    log.trace("exitArrayInit '{}'", ctx.getText());
    popTerminal("}");
    LinkedList<ParseItem> l = new LinkedList<>();
    for(;;) {
      if(nextTerminalIs("{")) {
        popTerminal("{");
        break;
      } else if(nextTerminalIs(",")) {
        popTerminal(",");
      } else if(nextItemIs(ArrayInit.class)) {
        l.addFirst(pop(ArrayInit.class));
      } else if(nextItemIsExpression()) {
        l.addFirst(popExpression());
      } else {
        fail("unexpected array initializer, " + stack.peek());
      }
    }
    stack.push(new ArrayInit(l));
  }

  @Override
  public void enterTypeOrPrimitiveOrVar(TypeOrPrimitiveOrVarContext ctx) {
    log.trace("enterTypeOrPrimitiveOrVar '{}'", ctx.getText());
  }

  @Override
  public void exitTypeOrPrimitiveOrVar(TypeOrPrimitiveOrVarContext ctx) {
    log.trace("exitTypeOrPrimitiveOrVar '{}'", ctx.getText());
    if(nextTerminalIs("var")) {
      popTerminal("var");
      stack.push(new TypeOrPrimitive());
    } else if(nextTerminalIs("val")) {
      popTerminal("val");
      stack.push(new TypeOrPrimitive(new org.ria.parser.Type(true)));
    } else if(nextItemIs(TypeOrPrimitive.class)) {
      // nothing to do, just keep TypeOrPrimitive on the stack
    } else {
      fail("expected TypeOrPrimitive or 'var' but stack has '%s'".formatted(stack.peek()));
    }
  }

  @Override
  public void enterHeaderElement(HeaderElementContext ctx) {
    log.trace("enterHeaderElement '{}'", ctx.getText());
  }

  @Override
  public void exitHeaderElement(HeaderElementContext ctx) {
    log.trace("exitHeaderElement '{}'", ctx.getText());
    if(nextItemIs(Statement.class)) {
      findMostRecentContainerStatement().addStatement(popStatement());
    }
  }

  @Override
  public void enterThrowStmt(ThrowStmtContext ctx) {
    log.trace("enterThrowStmt '{}'", ctx.getText());
  }

  @Override
  public void exitThrowStmt(ThrowStmtContext ctx) {
    log.trace("exitThrowStmt '{}'", ctx.getText());
    popSemi();
    Expression expr = popExpression();
    popTerminal("throw");
    stack.push(new ThrowStatement(ctx.getStart().getLine(), expr));
  }

  @Override
  public void enterTryStmt(TryStmtContext ctx) {
    log.trace("enterTryStmt '{}'", ctx.getText());
  }

  @Override
  public void exitTryStmt(TryStmtContext ctx) {
    log.trace("exitTryStmt '{}'", ctx.getText());
    FinallyBlock fblock = null;
    if(nextItemIs(FinallyBlock.class)) {
      fblock = pop(FinallyBlock.class);
    }
    LinkedList<CatchBlock> cblocks = new LinkedList<>();
    for(;;) {
      if(nextItemIs(CatchBlock.class)) {
        cblocks.addFirst(pop(CatchBlock.class));
      } else {
        break;
      }
    }
    TryStatement tryStmt = new TryStatement(ctx.getStart().getLine());
    tryStmt.setFinallyBlock(fblock);
    tryStmt.setCatchBlocks(cblocks);
    tryStmt.setBlock(pop(BlockStatement.class));
    LinkedList<TryResource> resources = new LinkedList<>();
    if(nextTerminalIs(")")) {
      popTerminal(")");
      for(;;) {
        if(nextItemIs(TryResource.class)) {
          resources.add(pop(TryResource.class));
        } else if(nextTerminalIs(";")) {
          // remove the semicolon that separates the AutoCloseable resources
          popTerminal(";");
        } else {
          break;
        }
      }
      popTerminal("(");
    }
    tryStmt.setResources(resources);
    popTerminal("try");
    stack.push(tryStmt);
  }

  @Override
  public void enterTryResource(TryResourceContext ctx) {
    log.trace("enterTryResource '{}'", ctx.getText());
  }

  @Override
  public void exitTryResource(TryResourceContext ctx) {
    log.trace("exitTryResource '{}'", ctx.getText());
    Expression expr = popExpression();
    popTerminal("=");
    Identifier ident = popIdentifier();
    TypeOrPrimitive type = pop(TypeOrPrimitive.class);
    stack.push(new TryResource(type.getType(), ident, expr));
  }

  @Override
  public void enterCatchBlock(CatchBlockContext ctx) {
    log.trace("enterCatchBlock '{}'", ctx.getText());
  }

  @Override
  public void exitCatchBlock(CatchBlockContext ctx) {
    log.trace("exitCatchBlock '{}'", ctx.getText());
    BlockStatement block = pop(BlockStatement.class);
    popTerminal(")");
    Identifier ident = popIdentifier();
    LinkedList<org.ria.parser.Type> types = new LinkedList<>();
    for(;;) {
      if(nextItemIs(Type.class)) {
        Type type = pop(Type.class);
        org.ria.parser.Type t = new org.ria.parser.Type(type.getIdent(), 0);
        types.addFirst(t);
        popTerminalIfExists("|");
      } else {
        break;
      }
    }
    popTerminal("(");
    popTerminal("catch");
    CatchBlock cblock = new CatchBlock(types, ident.getIdent(), block);
    stack.push(cblock);
  }

  @Override
  public void enterFinallyBlock(FinallyBlockContext ctx) {
    log.trace("enterFinallyBlock '{}'", ctx.getText());
  }

  @Override
  public void exitFinallyBlock(FinallyBlockContext ctx) {
    log.trace("exitFinallyBlock '{}'", ctx.getText());
    BlockStatement block = pop(BlockStatement.class);
    popTerminal("finally");
    FinallyBlock fblock = new FinallyBlock(block);
    stack.push(fblock);
  }

  @Override
  public void enterSwitchExpr(SwitchExprContext ctx) {
    log.trace("enterSwitchExpr '{}'", ctx.getText());
    stack.push(new SwitchExpression());
  }

  @Override
  public void exitSwitchExpr(SwitchExprContext ctx) {
    log.trace("exitSwitchExpr '{}'", ctx.getText());
    popTerminal("}");
    popTerminal("{");
    popTerminal(")");
    Expression expr = popExpression();
    popTerminal("(");
    popTerminal("switch");
    SwitchExpression s = (SwitchExpression)stack.peek();
    s.setSwitchExpression(expr);
  }

  @Override
  public void enterCases(CasesContext ctx) {
    log.trace("enterCases '{}'", ctx.getText());
  }

  @Override
  public void exitCases(CasesContext ctx) {
    log.trace("exitCases '{}'", ctx.getText());
  }

  @Override
  public void enterColonCase(ColonCaseContext ctx) {
    log.trace("enterColonCase '{}'", ctx.getText());
    stack.push(new BlockStatement((ctx.getStart().getLine())));
  }

  @Override
  public void exitColonCase(ColonCaseContext ctx) {
    log.trace("exitColonCase '{}'", ctx.getText());
    popTerminal(":");
    if(nextTerminalIs("default")) {
      popTerminal("default");
      BlockStatement block = pop(BlockStatement.class);
      SwitchExpression s = findMostRecent(SwitchExpression.class);
      SwitchColonCase c = new SwitchColonCase(null, block);
      s.addColonCase(c);
    } else {
      Expression exp = popExpression();
      popTerminal("case");
      BlockStatement block = pop(BlockStatement.class);
      SwitchExpression s = findMostRecent(SwitchExpression.class);
      SwitchColonCase c = new SwitchColonCase(exp, block);
      s.addColonCase(c);
    }
  }

  @Override
  public void enterArrowCase(ArrowCaseContext ctx) {
    log.trace("enterArrowCase '{}'", ctx.getText());
    stack.push(new BlockStatement((ctx.getStart().getLine())));
  }

  private BlockStatement arrowCaseBody(int line) {
    BlockStatement block = findMostRecent(BlockStatement.class);
    if(nextTerminalIs(";")) {
      popTerminal(";");
      Expression exp = popExpression();
      block.addStatement(new ExpressionStatement(line, exp));
    }
    return block;
  }

  @Override
  public void exitArrowCase(ArrowCaseContext ctx) {
    log.trace("exitArrowCase '{}'", ctx.getText());
    BlockStatement block = arrowCaseBody(ctx.getStart().getLine());
    popTerminal("->");
    LinkedList<Expression> caseExpressions = new LinkedList<>();
    if(nextTerminalIs("default")) {
      caseExpressions = null;
      popTerminal("default");
    } else {
      for(;;) {
        if(nextTerminalIs("case")) {
          popTerminal("case");
          break;
        } else {
          caseExpressions.addFirst(popExpression());
          if(nextTerminalIs(",")) {
            popTerminal(",");
          }
        }
      }
    }
    pop(BlockStatement.class);
    SwitchExpression s = findMostRecent(SwitchExpression.class);
    s.addArrowCase(new SwitchArrowCase(caseExpressions, block));
  }

  @Override
  public void enterSwitchStmt(SwitchStmtContext ctx) {
    log.trace("enterSwitchStmt '{}'", ctx.getText());
  }

  @Override
  public void exitSwitchStmt(SwitchStmtContext ctx) {
    log.trace("exitSwitchStmt '{}'", ctx.getText());
    SwitchExpression s = pop(SwitchExpression.class);
    ExpressionStatement stmt = new ExpressionStatement(ctx.getStart().getLine(), s);
    stack.push(stmt);
  }

  @Override
  public void enterYieldStmt(YieldStmtContext ctx) {
    log.trace("enterYieldStmt '{}'", ctx.getText());
  }

  @Override
  public void exitYieldStmt(YieldStmtContext ctx) {
    log.trace("exitYieldStmt '{}'", ctx.getText());
    popSemi();
    Expression expr = popExpression();
    popTerminal("yield");
    stack.push(new YieldStatement(ctx.getStart().getLine(), expr));
  }

  @Override
  public void enterVoidLiteral(VoidLiteralContext ctx) {
    log.trace("enterVoidLiteral '{}'", ctx.getText());
  }

  @Override
  public void exitVoidLiteral(VoidLiteralContext ctx) {
    log.trace("exitVoidLiteral '{}'", ctx.getText());
    popTerminal("void");
    stack.push(new VoidLiteral());
  }

  //  https://stackoverflow.com/a/58719524/20615256
//  private String getFullText(ParserRuleContext context) {
//    if(StringUtils.isBlank(context.getText())) {
//      return "";
//    }
//    if(context.start == null ||
//        context.stop == null ||
//        context.start.getStartIndex() < 0 ||
//        context.stop.getStopIndex() < 0)
//      return context.getText();
//    return context.start.getInputStream().getText(
//        Interval.of(context.start.getStartIndex(), context.stop.getStopIndex()));
//  }

  @Override
  public void enterJavaTypeDef(JavaTypeDefContext ctx) {
    log.trace("enterJavaTypeDef '{}'", ctx.getText());
  }

  @Override
  public void exitJavaTypeDef(JavaTypeDefContext ctx) {
    log.trace("exitJavaTypeDef '{}'", ctx.getText());
    popSemi();
    Expression expr = popExpression();
    popTerminal("javasrc");
    headerExit.addJavaType(new JavaTypeSource(expr));
    stack.push(new EmptyStatement(ctx.getStart().getLine()));
  }

  @Override
  public void enterObjectScopeStmt(ObjectScopeStmtContext ctx) {
    log.trace("enterObjectScopeStmt '{}'", ctx.getText());
    if(isObjectScopeExpressionWithStatements(ctx)) {
      stack.push(new BlockStatement(ctx.getStart().getLine()));
    }
  }

  @Override
  public void exitObjectScopeStmt(ObjectScopeStmtContext ctx) {
    log.trace("exitObjectScopeStmt '{}'", ctx.getText());
    stack.push(new ExpressionStatement(ctx.getStart().getLine(), parseObjectScopeExpression(ctx)));
  }

}
