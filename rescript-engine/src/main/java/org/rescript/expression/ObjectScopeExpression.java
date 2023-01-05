package org.rescript.expression;

import java.util.List;
import java.util.function.Consumer;

import org.rescript.run.ScriptContext;
import org.rescript.value.Value;
import org.rescript.value.VoidValue;

public class ObjectScopeExpression implements Expression {

  private Expression expression;

  private List<Expression> expressions;

  public ObjectScopeExpression(Expression expression, List<Expression> expressions) {
    super();
    this.expression = expression;
    this.expressions = expressions;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public Value eval(ScriptContext ctx) {
    Value v = expression.eval(ctx);
    if(v.isNull()) {
      return v;
    }
    final Object o = v.val();
    expressions.forEach(expr -> {
      Value v2 = expr.eval(ctx);
      if(!VoidValue.VOID.equals(v2)) {
        if(o instanceof Consumer c) {
          c.accept(v2.val());
        }
      }
    });
    return v;
  }

}
