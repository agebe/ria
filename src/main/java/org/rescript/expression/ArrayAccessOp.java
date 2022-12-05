package org.rescript.expression;

import java.util.List;

import org.rescript.ScriptException;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArrayAccessOp implements Expression {

  private static final Logger log = LoggerFactory.getLogger(ArrayAccessOp.class);

  private Expression arrayExpr;

  private Expression indexExpr;

  public ArrayAccessOp(Expression arrayExpr, Expression indexExpr) {
    super();
    this.arrayExpr = arrayExpr;
    this.indexExpr = indexExpr;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value v = arrayExpr.eval(ctx);
    Value index = indexExpr.eval(ctx);
    int i = index.toInt();
    log.debug("array access operation on value '{}'", v);
    if(v.isArray()) {
      return v.toArray().get(i);
    } else {
      Object o = v.val();
      if(o instanceof List<?> l) {
        return Value.of(l.get(i));
      } else {
        throw new ScriptException("array or list expected from expression but got '%s'".formatted(v.type()));
      }
    }
  }

}
