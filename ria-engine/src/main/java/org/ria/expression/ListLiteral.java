package org.ria.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.ria.run.ScriptContext;
import org.ria.value.Value;

public class ListLiteral implements Expression {

  private List<Expression> expressions;

  public ListLiteral(List<Expression> expressions) {
    super();
    this.expressions = expressions;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    List<?> l = expressions.stream()
        .map(expr -> expr.eval(ctx).val())
        .collect(Collectors.toCollection(ArrayList::new));
    return Value.of(List.class, l);
  }

}
