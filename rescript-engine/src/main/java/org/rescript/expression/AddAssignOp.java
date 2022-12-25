package org.rescript.expression;

import org.rescript.ScriptException;
import org.rescript.run.ScriptContext;
import org.rescript.symbol.VarSymbol;
import org.rescript.value.Value;

public class AddAssignOp implements Expression {

  private Identifier identifier;

  private Expression expression;

  public AddAssignOp(Identifier identifier, Expression expression) {
    super();
    this.identifier = identifier;
    this.expression = expression;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    VarSymbol variable = ctx.getSymbols()
        .getScriptSymbols()
        .resolveVar(identifier.getIdent());
    if(variable == null) {
      throw new ScriptException("unknown variable '%s'".formatted(identifier.getIdent()));
    }
    Value v1 = variable.getVal();
    Value v2 = expression.eval(ctx);
    Value result = AddOp.add(v1, v2, "+=");
    variable.setVal(result);
    return result;
  }

}
