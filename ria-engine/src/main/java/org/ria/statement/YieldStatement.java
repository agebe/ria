package org.ria.statement;

import org.ria.expression.Expression;

// added the yield statement for convenience so it can appear in switch expressions
// (but can also be used anywhere else where statements can appear)
// it only executes the expression and stores the result in the script context.
public class YieldStatement extends ExpressionStatement {

  public YieldStatement(int lineNumber, Expression expression) {
    super(lineNumber, expression);
  }

  @Override
  public String toString() {
    return "YieldStatement []";
  }

}
