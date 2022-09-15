package org.rescript.parser;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.rescript.ScriptException;
import org.rescript.antlr.ScriptParser.ExprContext;
import org.rescript.expression.DotOperator;
import org.rescript.expression.Expression;
import org.rescript.expression.TargetExpression;

public class ExpressionParser {

  private List<ParseItem> items;

  private Deque<ParseItem> stack;

  public ExpressionParser(ExprContext ctx, Deque<ParseItem> stack) {
    super();
    this.stack = stack;
    this.items = items(ctx, stack);
  }

  private static List<ParseItem> items(ExprContext ctx, Deque<ParseItem> stack) {
    LinkedList<ParseItem> items = new LinkedList<>();
    for(;;) {
      ParseItem item = stack.pop();
      if(item instanceof ExpressionStartMarker) {
        ExpressionStartMarker expr = (ExpressionStartMarker)item;
        if(expr.getCtx().equals(ctx)) {
          break;
        }
      }
      items.addFirst(item);
    }
    return items;
  }

  private void fail(String msg) {
    throw new ScriptException(msg);
  }

  private boolean isTerminal(int index, String text) {
    if(items.get(index) instanceof Terminal) {
      Terminal t = (Terminal)items.get(index);
      return text.equals(t.getText());
    } else {
      return false;
    }
  }

  private boolean isExpression(int index) {
    return items.get(index) instanceof Expression;
  }

  private Expression getExpression(int index) {
    return (Expression)items.get(index);
  }

  private boolean isSingle() {
    return items.size() == 1;
  }

  private boolean isDouble() {
    return items.size() == 2;
  }

  private boolean isTriple() {
    return items.size() == 3;
  }

  private boolean isSingleExpression() {
    return (items.size() == 1) && items.get(0) instanceof Expression;
  }

  private boolean isParens() {
    return isTerminal(0, "(") && isExpression(1) && isTerminal(2, ")");
  }

  private boolean isDotOp() {
    return isExpression(0) && isTerminal(1, ".") && isExpression(2);
  }

  private Expression exp(int i) {
    return getExpression(i);
  }

  private TargetExpression texp(int i) {
    return (TargetExpression)getExpression(i);
  }

  public void parse() {
    // only 1 item on the stack, we just push that back as there is nothing else to do
    if(isSingle()) {
      if(isSingleExpression()) {
        stack.push(getExpression(0));
      } else {
        fail("expected expression but got '%s'".formatted(items));
      }
    } else if(isDouble()) {
      // FIXME
      fail("not implemented");
    } else if(isTriple()) {
//parentheses
      if(isParens()) {
        stack.push(getExpression(1));
      } else
// dot (member) operator
      if(isDotOp()) {
        stack.push(new DotOperator(exp(0), texp(2)));
      } else
        fail("failed to parse expression (unknown, 3), '%s'".formatted(items));
    } else {
      fail("failed to parse expression (unknown, '%s'), '%s'".formatted(items.size(), items));
    }
  }

}
