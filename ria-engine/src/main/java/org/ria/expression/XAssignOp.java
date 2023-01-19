package org.ria.expression;

import org.ria.ScriptException;
import org.ria.run.ScriptContext;
import org.ria.symbol.VarSymbol;
import org.ria.value.Value;

public abstract class XAssignOp implements Expression {

  private Identifier identifier;

  private Expression expression;

  public XAssignOp(Identifier identifier, Expression expression) {
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
    Value result = doOp(v1, v2);
    variable.setVal(result);
    return result;
  }

  protected abstract Value doOp(Value v1, Value v2);

}
