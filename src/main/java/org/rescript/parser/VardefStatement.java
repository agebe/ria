package org.rescript.parser;

import org.rescript.run.Expressions;
import org.rescript.symbol.SymbolTable;

public class VardefStatement implements ParseItem, Statement {

  private String name;

  private Expression initial;

  public VardefStatement(String name, Expression initial) {
    super();
    this.name = name;
    this.initial = initial;
  }

  public void execute(SymbolTable symbols, Expressions expr) {
    symbols.defineVar(name, (initial!=null?initial.eval(expr):null));
  }

  @Override
  public String toString() {
    return "VardefStatement [name=" + name + ", initial=" + initial + "]";
  }

}
