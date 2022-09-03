package io.github.agebe.script;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import io.github.agebe.script.lang.Expression;
import io.github.agebe.script.lang.FunctionCall;
import io.github.agebe.script.lang.FunctionName;
import io.github.agebe.script.lang.FunctionParameter;
import io.github.agebe.script.lang.Identifier;
import io.github.agebe.script.lang.LangItem;
import io.github.agebe.script.lang.StringLiteral;
import io.github.agebe.script.lang.Terminal;

public class ScriptExecutor implements ScriptListener {

  private static final Logger log = LoggerFactory.getLogger(ScriptExecutor.class);

  private Deque<LangItem> stack = new ArrayDeque<LangItem>();

  private SymbolTable symbols = new SymbolTable();

  private FunctionCaller caller = new FunctionCaller(symbols);

  private void log(String msg) {
    System.out.println(msg);
  }

  @Override
  public void visitTerminal(TerminalNode node) {
    log.debug("visit terminal '{}'", node.getSymbol().getText());
//    log("push terminal " + node.getSymbol().getText());
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
    log.debug("exit script");
  }

  @Override
  public void enterStmt(StmtContext ctx) {
    log.debug("enter stmt '{}'", ctx.getText());
  }

  @Override
  public void exitStmt(StmtContext ctx) {
    log.debug("exit stmt '{}'", ctx.getText());
    // TODO just for now to show something
    popTerminal();
    LangItem i = stack.pop();
    log(""+i);
    i.resolve();
  }

  @Override
  public void enterReturnStmt(ReturnStmtContext ctx) {
    log.debug("enter return stmt '{}'", ctx.getText());
  }

  @Override
  public void exitReturnStmt(ReturnStmtContext ctx) {
    log.debug("exit return stmt '{}'", ctx.getText());
  }

  @Override
  public void enterVardef(VardefContext ctx) {
    log.debug("enter vardef '{}'", ctx.getText());
  }

  @Override
  public void exitVardef(VardefContext ctx) {
    log.debug("exit vardef '{}'", ctx.getText());
  }

  @Override
  public void enterVarAssignStmt(VarAssignStmtContext ctx) {
    log.debug("enter var assign stmt '{}'", ctx.getText());
  }

  @Override
  public void exitVarAssignStmt(VarAssignStmtContext ctx) {
    log.debug("exit var assign stmt '{}'", ctx.getText());
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
    stack.push(new Expression(ctx));
  }

  @Override
  public void exitExpr(ExprContext ctx) {
    log.debug("exit expr '{}'", ctx.getText());
    LinkedList<LangItem> items = new LinkedList<>();
    for(;;) {
      LangItem item = stack.pop();
      if(item instanceof Expression) {
        Expression expr = (Expression)item;
        if(expr.getCtx().equals(ctx)) {
          break;
        }
      }
      items.addFirst(item);
    }
//    log("on exitExpr got items '%s'".formatted(items));
    // only 1 item on the stack, we just push that back as there is nothing else to do
    if(items.size() == 1) {
      stack.push(items.get(0));
    } else if(items.size() == 3) {
      // do we have an operator?
      if(items.get(1) instanceof Terminal) {
        Terminal operator = (Terminal)items.get(1);
        String op = operator.getToken().getText();
        if(".".equals(op)) {
          dotOperator(items.get(0), items.get(2));
        } else {
          fail("unknown operator '%s'".formatted(items));
        }
      } else {
        fail("exit expression, unimplemented case '%s'".formatted(items));
      }
    } else {
      fail("exit expression, unimplemented case '%s'".formatted(items));
    }
  }

  private void dotOperator(LangItem item1, LangItem item2) {
    if(item1 instanceof Identifier) {
      Identifier i1 = (Identifier)item1;
      if(item2 instanceof Identifier) {
        Identifier i2 = (Identifier)item2;
        stack.push(new Identifier(i1.getIdent() + "." + i2.getIdent(), symbols));
      } else if(item2 instanceof FunctionCall) {
        FunctionCall f = (FunctionCall)item2;
        stack.push(new FunctionCall(f.getName(), f.getParameters(), i1, caller));
      } else {
        fail("dot operator, unimplemented case '%s'.'%s'".formatted(item1, item2));
      }
    } else {
      fail("dot operator, unimplemented case '%s'.'%s'".formatted(item1, item2));
    }
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
      LangItem item = stack.pop();
      if(item instanceof FunctionParameter) {
        FunctionParameter param = (FunctionParameter)item;
        params.addFirst(param);
      } else if(item instanceof FunctionName) {
        FunctionName name = (FunctionName)item;
        stack.push(new FunctionCall(name, params, null, caller));
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
  }

  @Override
  public void exitFparams(FparamsContext ctx) {
    log.debug("exit fparams '{}'", ctx.getText());
  }

  @Override
  public void enterFparam(FparamContext ctx) {
    log.debug("enter fparam '{}'", ctx.getText());
    // get rid of initial left parenthesis or comma separating parameters
    String s = popTerminal().getToken().getText();
    if(!StringUtils.equalsAny(s, "(", ",")) {
      fail("expected left parenthesis or comma but got " + s);
    }
  }

  @Override
  public void exitFparam(FparamContext ctx) {
    log.debug("exit fparam '{}'", ctx.getText());
    stack.push(new FunctionParameter(stack.pop()));
  }

  @Override
  public void enterLiteral(LiteralContext ctx) {
    log.debug("enter literal '{}'", ctx.getText());
  }

  @Override
  public void exitLiteral(LiteralContext ctx) {
    log.debug("exit literal '{}'", ctx.getText());
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
  public void enterIdent(IdentContext ctx) {
    log.debug("enter ident '{}'", ctx.getText());
  }

  @Override
  public void exitIdent(IdentContext ctx) {
    log.debug("exit ident '{}'", ctx.getText());
    stack.push(new Identifier(popTerminal().getToken().getText(), symbols));
  }

  private void fail(String msg) {
    throw new ScriptException(msg);
  }

  private Terminal popTerminal() {
    LangItem terminal = stack.pop();
    if(!(terminal instanceof Terminal)) {
      fail("expected terminal but got '%s'".formatted(terminal));
    }
    return (Terminal)terminal;
  }

}
