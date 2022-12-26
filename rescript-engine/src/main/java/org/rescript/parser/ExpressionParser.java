package org.rescript.parser;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.rescript.ScriptException;
import org.rescript.antlr.ScriptParser.ExprContext;
import org.rescript.expression.AddAssignOp;
import org.rescript.expression.AddOp;
import org.rescript.expression.ArrayAccessOp;
import org.rescript.expression.ArrayLiteral;
import org.rescript.expression.BitAndAssignOp;
import org.rescript.expression.BitAndOp;
import org.rescript.expression.BitLeftShiftAssignOp;
import org.rescript.expression.BitLeftShiftOp;
import org.rescript.expression.BitNotOp;
import org.rescript.expression.BitOrAssignOp;
import org.rescript.expression.BitOrOp;
import org.rescript.expression.BitRightShiftAssignOp;
import org.rescript.expression.BitRightShiftOp;
import org.rescript.expression.BitUnsignedRightShiftAssignOp;
import org.rescript.expression.BitUnsignedRightShiftOp;
import org.rescript.expression.BitXorAssignOp;
import org.rescript.expression.BitXorOp;
import org.rescript.expression.CastOp;
import org.rescript.expression.ClassLiteral;
import org.rescript.expression.DivAssignOp;
import org.rescript.expression.DivOp;
import org.rescript.expression.DotOperator;
import org.rescript.expression.EqualityOp;
import org.rescript.expression.Expression;
import org.rescript.expression.GeOp;
import org.rescript.expression.GtOp;
import org.rescript.expression.Identifier;
import org.rescript.expression.InstanceOfOp;
import org.rescript.expression.LeOp;
import org.rescript.expression.ListLiteral;
import org.rescript.expression.LogicalAndOp;
import org.rescript.expression.LogicalOrOp;
import org.rescript.expression.LtOp;
import org.rescript.expression.MapLiteral;
import org.rescript.expression.ModAssignOp;
import org.rescript.expression.ModOp;
import org.rescript.expression.MulAssignOp;
import org.rescript.expression.MulOp;
import org.rescript.expression.SubAssignOp;
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
    return is(index, Expression.class);
  }

  private boolean isIdentifier(int index) {
    return is(index, Identifier.class);
  }

  private Expression getExpression(int index) {
    return (Expression)items.get(index);
  }

  private boolean is(int index, Class<?> type) {
    return type.isAssignableFrom(items.get(index).getClass());
  }

  private <T> T get(int index, Class<T> type) {
    return type.cast(items.get(index));
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
    return (items.size() == 1) && (items.get(0) instanceof Expression);
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

  private boolean isBitAndOp() {
    return isMiddleOp("&");
  }

  private boolean isBitOrOp() {
    return isMiddleOp("|");
  }

  private boolean isBitXorOp() {
    return isMiddleOp("^");
  }

  private boolean isLeftShift() {
    return isMiddleOp("<<");
  }

  private boolean isRightShift() {
    return isMiddleOp(">>");
  }

  private boolean isUnsignedRightShift() {
    return isMiddleOp(">>>");
  }

  private boolean isAssign(String assignOp) {
    return isIdentifier(0) &&
        isTerminal(1, assignOp) &&
        isExpression(2);
  }

  private boolean isAddAssign() {
    return isAssign("+=");
  }

  private boolean isSubAssign() {
    return isAssign("-=");
  }

  private boolean isMulAssign() {
    return isAssign("*=");
  }

  private boolean isDivAssign() {
    return isAssign("/=");
  }

  private boolean isModAssign() {
    return isAssign("%=");
  }

  private boolean isAndAssign() {
    return isAssign("&=");
  }

  private boolean isOrAssign() {
    return isAssign("|=");
  }

  private boolean isXorAssign() {
    return isAssign("^=");
  }

  private boolean isLShiftAssign() {
    return isAssign("<<=");
  }

  private boolean isRShiftAssign() {
    return isAssign(">>=");
  }

  private boolean isURShiftAssign() {
    return isAssign(">>>=");
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

  private Identifier ident(int i) {
    return get(i, Identifier.class);
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

  private boolean isTypeOf() {
    return isTerminal(0, "typeof") && isExpression(1);
  }

  private boolean isInstanceOf() {
    return (items.size() >= 3) && isTerminal(1, "instanceof") && isExpression(0) && is(2, TypeOrPrimitive.class);
  }

  private boolean isListLiteral() {
    for(int i=0;i<items.size();i++) {
      if(i == 0) {
        if(!isTerminal(0, "[")) {
          return false;
        }
      } else if(i == (items.size()-1)) {
        if(!isTerminal(items.size()-1, "]")) {
          return false;
        }
      } else if(i % 2 == 0) {
        if(!isTerminal(i, ",")) {
          return false;
        }
      } else {
        if(isTerminal(i)) {
          return false;
        }
      }
    }
    return true;
  }

  private ListLiteral listLiteral() {
    return new ListLiteral(items.stream()
        .filter(pi -> pi instanceof Expression)
        .map(pi -> (Expression)pi)
        .toList());
  }

  private boolean isArrayLiteral() {
    for(int i=0;i<items.size();i++) {
      if(i == 0) {
        if(!isTerminal(0, "arrayof")) {
          return false;
        }
      } else if(i == 1) {
        if(!isTerminal(1, "[")) {
          return false;
        }
      } else if(i == (items.size()-1)) {
        if(!isTerminal(items.size()-1, "]")) {
          return false;
        }
      } else if(i % 2 == 0) {
        if(isTerminal(i)) {
          return false;
        }
      } else {
        if(!isTerminal(i, ",")) {
          return false;
        }
      }
    }
    return true;
  }

  private ArrayLiteral arrayLiteral() {
    return new ArrayLiteral(items.stream()
        .filter(pi -> pi instanceof Expression)
        .map(pi -> (Expression)pi)
        .toList());
  }

  private boolean isArrayAccess() {
    return isExpression(0) && isTerminal(1, "[") && isExpression(2) && isTerminal(3, "]");
  }

  private boolean isMapLiteral() {
    if(items.size() == 3) {
      // empty map literal
      return isTerminal(0, "[") &&
          isTerminal(1, ":") &&
          isTerminal(2, "]");
    } else {
      if(!isTerminal(0, "[")) {
        return false;
      }
      if(!isTerminal(items.size()-1, "]")) {
        return false;
      }
      for(int i=1;i<items.size();i+=4) {
        if(!(
            isExpression(i) &&
            isTerminal(i+1, ":") &&
            isExpression(i+2) &&
            ((i+3)==items.size()-1)?isTerminal(i+3, "]"):isTerminal(i+3, ","))) {
          return false;
        }
      }
      return true;
    }
  }

  private MapLiteral mapLiteral() {
    if(items.size() == 3) {
      return new MapLiteral(Collections.emptyList());
    } else {
      List<Map.Entry<Expression, Expression>> entries = new ArrayList<>();
      for(int i=1;i<items.size();i+=4) {
        Expression keyExp = exp(i);
        Expression valExp = exp(i+2);
        entries.add(new AbstractMap.SimpleImmutableEntry<Expression, Expression>(keyExp, valExp));
      }
      return new MapLiteral(entries);
    }
  }

  private boolean isCast() {
    return items.size() == 4 &&
        isTerminal(0, "(") &&
        (items.get(1) instanceof TypeOrPrimitive) &&
        isTerminal(2, ")") &&
        isExpression(3);
  }

  private boolean isClassLiteral() {
    return items.size() == 3 &&
        is(0, TypeOrPrimitive.class) &&
        isTerminal(1, ".") &&
        isTerminal(2, "class");
  }

  public void parse() {
    if(isClassLiteral()) {
      stack.push(new ClassLiteral(((TypeOrPrimitive)items.get(0)).getType()));
    } else if(isInstanceOf()) {
      stack.push(new InstanceOfOp(
          exp(0),
          get(2, TypeOrPrimitive.class),
          (items.size() == 4)?(Identifier)items.get(3):null));
    } else if(isListLiteral()) {
      stack.push(listLiteral());
    } else if(isArrayLiteral()) {
      stack.push(arrayLiteral());
    } else if(isMapLiteral()) {
      stack.push(mapLiteral());
    } else if(isSingle()) {
   // only 1 item on the stack, we just push that back as there is nothing else to do
      if(isSingleExpression()) {
        stack.push(getExpression(0));
      } else {
        fail("expected single expression but got '%s'".formatted(items));
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
          stack.push(new BitNotOp(exp(1)));
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
      } else if(isBitAndOp()) {
        stack.push(new BitAndOp(exp(0), exp(2)));
      } else if(isBitOrOp()) {
        stack.push(new BitOrOp(exp(0), exp(2)));
      } else if(isBitXorOp()) {
        stack.push(new BitXorOp(exp(0), exp(2)));
      } else if(isLeftShift()) {
        stack.push(new BitLeftShiftOp(exp(0), exp(2)));
      } else if(isRightShift()) {
        stack.push(new BitRightShiftOp(exp(0), exp(2)));
      } else if(isUnsignedRightShift()) {
        stack.push(new BitUnsignedRightShiftOp(exp(0), exp(2)));
      } else if(isAddAssign()) {
        stack.push(new AddAssignOp(ident(0), exp(2)));
      } else if(isSubAssign()) {
        stack.push(new SubAssignOp(ident(0), exp(2)));
      } else if(isMulAssign()) {
        stack.push(new MulAssignOp(ident(0), exp(2)));
      } else if(isDivAssign()) {
        stack.push(new DivAssignOp(ident(0), exp(2)));
      } else if(isModAssign()) {
        stack.push(new ModAssignOp(ident(0), exp(2)));
      } else if(isAndAssign()) {
        stack.push(new BitAndAssignOp(ident(0), exp(2)));
      } else if(isOrAssign()) {
        stack.push(new BitOrAssignOp(ident(0), exp(2)));
      } else if(isXorAssign()) {
        stack.push(new BitXorAssignOp(ident(0), exp(2)));
      } else if(isLShiftAssign()) {
        stack.push(new BitLeftShiftAssignOp(ident(0), exp(2)));
      } else if(isRShiftAssign()) {
        stack.push(new BitRightShiftAssignOp(ident(0), exp(2)));
      } else if(isURShiftAssign()) {
        stack.push(new BitUnsignedRightShiftAssignOp(ident(0), exp(2)));
      } else {
        fail("failed to parse expression (unknown, 3), '%s'".formatted(items));
      }
    } else if(isTernaryOp()) {
      stack.push(new TernaryOp(exp(0), exp(2), exp(4)));
    } else if(isArrayAccess()) {
      stack.push(new ArrayAccessOp(exp(0), exp(2)));
    } else if(isCast()) {
      TypeOrPrimitive type = (TypeOrPrimitive)items.get(1);
      stack.push(new CastOp(type.getType(), exp(3)));
    } else {
      fail("failed to parse expression (unknown, '%s'), '%s'".formatted(items.size(), items));
    }
  }

}
