package org.rescript.parser;

import org.rescript.run.Expressions;
import org.rescript.run.Value;

// an expression that can be executed on a target (dot operator)
public interface TargetExpression extends Expression {
  Value eval(Expressions expressions, Value target);
}
