package org.rescript.statement;

import org.rescript.parser.Expression;
import org.rescript.parser.ParseItem;
import org.rescript.run.Expressions;
import org.rescript.run.ScriptContext;
import org.rescript.symbol.SymbolTable;

public class VardefStatement implements Statement {

  private String name;

  private Expression initial;

  public VardefStatement(String name, Expression initial) {
    super();
    this.name = name;
    this.initial = initial;
  }

  @Override
  public void execute(ScriptContext ctx) {
    execute(ctx.getSymbols(), ctx.getExpressions());
    ctx.setCurrent(ctx.getCurrent().getTrueNode());
  }

  private void execute(SymbolTable symbols, Expressions expr) {
    symbols.defineVar(name, (initial!=null?initial.eval(expr):null));
  }

  @Override
  public String toString() {
    return "VardefStatement [name=" + name + ", initial=" + initial + "]";
  }

}
