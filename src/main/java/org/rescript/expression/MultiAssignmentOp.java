package org.rescript.expression;

import java.util.List;

import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

public class MultiAssignmentOp implements Expression {

  private List<Identifier> identifiers;

  private Expression expr;

  public MultiAssignmentOp(List<Identifier> identifiers, Expression expr) {
    super();
    this.identifiers = identifiers;
    this.expr = expr;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value v = expr.eval(ctx);
    // TODO if value is an array, assign array elements to variables
    // nothing to do if there are not enough variables, extra array elements are ignored in this case
    // if there are to many variables, assign the whole array to the each of them
    for(int i=0;i<identifiers.size();i++) {
      ctx.getSymbols().getScriptSymbols().assignVar(identifiers.get(i).getIdent(), v);
    }
    return v;
  }

}
