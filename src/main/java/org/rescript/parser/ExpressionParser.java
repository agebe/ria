package org.rescript.parser;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.rescript.ScriptException;
import org.rescript.antlr.ScriptParser.ExprContext;
import org.rescript.expression.AddOp;
import org.rescript.expression.DivOp;
import org.rescript.expression.DotOperator;
import org.rescript.expression.DottedIdentifier;
import org.rescript.expression.EqualityOp;
import org.rescript.expression.Expression;
import org.rescript.expression.GeOp;
import org.rescript.expression.GtOp;
import org.rescript.expression.Identifier;
import org.rescript.expression.InstanceOfOp;
import org.rescript.expression.LeOp;
import org.rescript.expression.LogicalAndOp;
import org.rescript.expression.LogicalOrOp;
import org.rescript.expression.LtOp;
import org.rescript.expression.ModOp;
import org.rescript.expression.MulOp;
import org.rescript.expression.SubOp;
import org.rescript.expression.TargetExpression;
import org.rescript.expression.TernaryOp;
import org.rescript.expression.TypeOfOp;
import org.rescript.expression.UnaryLogicalNotOp;
import org.rescript.expression.UnaryMinusOp;
import org.rescript.expression.UnaryPlusOp;
import org.rescript.expression.UnaryPostDecOp;
import org.rescript.expression.UnaryPostIncOp;
import org.rescript.expression.UnaryPreDecOp;
import org.rescript.expression.UnaryPreIncOp;

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

  private void failUnknownExpression() {
    fail("failed to parse expression (unknown, '%s'), '%s'".formatted(items.size(), items));
  }

  private boolean isTerminal(int index) {
    return items.get(index) instanceof Terminal;
  }

  private boolean isTerminal(int index, String text) {
    return isTerminalAnyOf(index, text);
  }

  private boolean isTerminalAnyOf(int index, String... text) {
    return isTerminal(index)?StringUtils.equalsAny(terminal(index), text):false;
  }

  private String terminal(int index) {
    return ((Terminal)items.get(index)).getText();
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

  private boolean isMiddleOp(String... op) {
    return isExpression(0) && isTerminalAnyOf(1, op) && isExpression(2);
  }

  private boolean isMiddleOp(String op) {
    return isExpression(0) && isTerminal(1, op) && isExpression(2);
  }

  private boolean isDotOp() {
    return isMiddleOp(".");
  }

  private boolean isLogicalAnd() {
    return isMiddleOp("&&");
  }

  private boolean isLogicalOr() {
    return isMiddleOp("||");
  }

  private boolean isArith() {
    return isMiddleOp("*", "/", "%", "+", "-");
  }

  private boolean isEquality() {
    return isMiddleOp("==", "!=");
  }

  private boolean isRelational() {
    return isMiddleOp(">", "<", ">=", "<=");
  }

  private boolean isUnaryPlus() {
    return isTerminal(0, "+");
  }

  private boolean isUnaryMinus() {
    return isTerminal(0, "-");
  }

  private boolean isUnaryLogicalNot() {
    return isTerminal(0, "!");
  }

  private boolean isUnaryBinaryNot() {
    return isTerminal(0, "~");
  }

  private boolean isUnaryPreInc() {
    return isTerminal(0, "++");
  }

  private boolean isUnaryPreDec() {
    return isTerminal(0, "--");
  }

  private boolean isUnaryPostInc() {
    return isTerminal(1, "++");
  }

  private boolean isUnaryPostDec() {
    return isTerminal(1, "--");
  }

  private boolean isTernaryOp() {
    return isExpression(0) &&
        isTerminal(1, "?") &&
        isExpression(2) &&
        isTerminal(3, ":") &&
        isExpression(4);
  }

  private Expression exp(int i) {
    return getExpression(i);
  }

  private TargetExpression texp(int i) {
    return (TargetExpression)getExpression(i);
  }

  private Expression parseArith() {
    Expression exp1 = exp(0);
    Expression exp2 = exp(2);
    String op = terminal(1);
    if(op.equals("*")) {
      return new MulOp(exp1, exp2);
    } else if(op.equals("/")) {
      return new DivOp(exp1, exp2);
    } else if(op.equals("%")) {
      return new ModOp(exp1, exp2);
    } else if(op.equals("+")) {
      return new AddOp(exp1, exp2);
    } else if(op.equals("-")) {
      return new SubOp(exp1, exp2);
    } else {
      fail("unknown operator '%s'".formatted(op));
      return null;
    }
  }

  private Expression parseRelational() {
    Expression exp1 = exp(0);
    Expression exp2 = exp(2);
    String op = terminal(1);
    if(op.equals(">")) {
      return new GtOp(exp1, exp2);
    } else if(op.equals("<")) {
      return new LtOp(exp1, exp2);
    } else if(op.equals(">=")) {
      return new GeOp(exp1, exp2);
    } else if(op.equals("<=")) {
      return new LeOp(exp1, exp2);
    } else {
      fail("unknown relational operator '%s'".formatted(op));
      return null;
    }
  }

  private boolean isDottedIdentifier() {
    if(items.size() < 3) {
      return false;
    }
    for(int i=0;i<items.size();i++) {
      if((i % 2) == 0) {
        if(!(items.get(i) instanceof Identifier)) {
          return false;
        }
      } else {
        // odd needs to be a dot terminal
        if(!isTerminal(i, ".")) {
          return false;
        }
      }
    }
    return true;
  }

  private DottedIdentifier dottedIdentifier() {
    return new DottedIdentifier(
        items
        .stream()
        .map(ParseItem::getText)
        .collect(Collectors.joining()));
  }

  private boolean isTypeOf() {
    return isTerminal(0, "typeof") && isExpression(1);
  }

  private boolean isInstanceOf() {
    return (items.size() >= 3) && isTerminal(1, "instanceof") && isExpression(0) && isExpression(2);
  }

  public void parse() {
    if(isDottedIdentifier()) {
      stack.push(dottedIdentifier());
    } else if(isInstanceOf()) {
      stack.push(new InstanceOfOp(exp(0), exp(2), (items.size() == 4)?(Identifier)items.get(3):null));
    } else if(isSingle()) {
   // only 1 item on the stack, we just push that back as there is nothing else to do
      if(isSingleExpression()) {
        stack.push(getExpression(0));
      } else {
        fail("expected expression but got '%s'".formatted(items));
      }
    } else if(isDouble()) {
      if(isTerminal(0)) {
        if(isUnaryPlus()) {
          stack.push(new UnaryPlusOp(exp(1)));
        } else if(isUnaryMinus()) {
          stack.push(new UnaryMinusOp(exp(1)));
        } else if(isUnaryLogicalNot()) {
          stack.push(new UnaryLogicalNotOp(exp(1)));
        } else if(isUnaryBinaryNot()) {
          fail("not implemented yet");
        } else if(isUnaryPreInc()) {
          stack.push(new UnaryPreIncOp(exp(1)));
        } else if(isUnaryPreDec()) {
          stack.push(new UnaryPreDecOp(exp(1)));
        } else if(isTypeOf()) {
          stack.push(new TypeOfOp(exp(1)));
        } else {
          failUnknownExpression();
        }
      } else if(isTerminal(1)) {
        if(isUnaryPostInc()) {
          stack.push(new UnaryPostIncOp(exp(0)));
        } else if(isUnaryPostDec()) {
          stack.push(new UnaryPostDecOp(exp(0)));
        } else {
          failUnknownExpression();
        }
      } else {
        failUnknownExpression();
      }
    } else if(isTriple()) {
      if(isParens()) {
        stack.push(getExpression(1));
      } else if(isDotOp()) {
        stack.push(new DotOperator(exp(0), texp(2)));
      } else if(isArith()) {
        stack.push(parseArith());
      } else if(isLogicalAnd()) {
        stack.push(new LogicalAndOp(exp(0), exp(2)));
      } else if(isLogicalOr()) {
        stack.push(new LogicalOrOp(exp(0), exp(2)));
      } else if(isEquality()) {
        stack.push(new EqualityOp(exp(0), exp(2), terminal(1)));
      } else if(isRelational()) {
        stack.push(parseRelational());
      } else {
        fail("failed to parse expression (unknown, 3), '%s'".formatted(items));
      }
    } else if(isTernaryOp()) {
      stack.push(new TernaryOp(exp(0), exp(2), exp(4)));
    } else {
      fail("failed to parse expression (unknown, '%s'), '%s'".formatted(items.size(), items));
    }
  }

}
