package org.rescript.expression;

import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

// an expression that can be executed on a target (dot operator)
public interface TargetExpression extends Expression {
  Value eval(ScriptContext ctx, Value target);
}
