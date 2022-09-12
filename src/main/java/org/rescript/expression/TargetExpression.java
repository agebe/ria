package org.rescript.expression;

import org.rescript.run.Expressions;
import org.rescript.value.Value;

// an expression that can be executed on a target (dot operator)
public interface TargetExpression extends Expression {
  Value eval(Expressions expressions, Value target);
}
