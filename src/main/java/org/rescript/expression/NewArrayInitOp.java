package org.rescript.expression;

import java.util.List;

import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

public class NewArrayInitOp implements Expression {
  private String type;

  private List<Expression> elements;

  public NewArrayInitOp(String type, List<Expression> elements) {
    super();
    this.type = type;
    this.elements = elements;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    List<Value> values = elements.stream()
        .map(expr -> expr.eval(ctx))
        .toList();
    return ArrayUtil.newArray(ctx.getSymbols().getJavaSymbols().resolveType(type), values);
  }
}
