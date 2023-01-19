package org.ria.expression;

import java.util.List;

import org.ria.run.ScriptContext;
import org.ria.value.Array;
import org.ria.value.Value;

public class MultiAssignmentOp implements Assignment {

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
    // if value is an array or list, assign elements to variables
    // nothing to do if there are not enough variables, extra array elements are ignored in this case
    // if there are to many variables, assign the whole array/list to the each of them
    if(v instanceof Array a) {
      for(int i=0;i<identifiers.size();i++) {
        ctx.getSymbols().getScriptSymbols().assignVar(identifiers.get(i).getIdent(),
            (i<a.length()?a.get(i):v));
      }
    } else if(v.val() instanceof List<?> l) {
      for(int i=0;i<identifiers.size();i++) {
        ctx.getSymbols().getScriptSymbols().assignVar(identifiers.get(i).getIdent(),
            (i<l.size()?Value.of(l.get(i)):v));
      }
    } else {
      for(int i=0;i<identifiers.size();i++) {
        ctx.getSymbols().getScriptSymbols().assignVar(identifiers.get(i).getIdent(), v);
      }
    }
    return v;
  }

  @Override
  public List<Identifier> identifiers() {
    return identifiers;
  }

}
