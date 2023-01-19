package org.ria.expression;

import java.util.List;
import java.util.function.Consumer;

import org.ria.run.ScriptContext;
import org.ria.statement.BlockStatement;
import org.ria.value.Value;
import org.ria.value.VoidValue;

public class ObjectScopeExpression implements Expression {

  private Expression expression;

  private List<Expression> expressions;

  private BlockStatement block;

  public ObjectScopeExpression(Expression expression, List<Expression> expressions, BlockStatement block) {
    super();
    this.expression = expression;
    this.expressions = expressions;
    this.block = block;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public Value eval(ScriptContext ctx) {
    Value v = expression.eval(ctx);
    if(v.isNull()) {
      return v;
    }
    final Object o = v.val();
    try {
      ctx.getSymbols().getScriptSymbols().enterObjectScope(o);
      if(block != null) {
        block.execute(ctx);
      } else {
        expressions.forEach(expr -> {
          Value v2 = expr.eval(ctx);
          if(!VoidValue.VOID.equals(v2)) {
            if(o instanceof Consumer c) {
              c.accept(v2.val());
            }
          }
        });
      }
      return v;
    } finally {
      ctx.getSymbols().getScriptSymbols().exitScope();
    }
  }

}
