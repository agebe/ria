package org.ria.expression;

import org.ria.run.ScriptContext;
import org.ria.value.Value;

// an expression that can be executed on a target (dot operator)
public interface TargetExpression extends Expression {
  Value eval(ScriptContext ctx, Value target);
}
