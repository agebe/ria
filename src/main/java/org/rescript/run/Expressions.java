package org.rescript.run;

import org.rescript.ScriptException;
import org.rescript.symbol.ClassSymbol;
import org.rescript.symbol.Symbol;
import org.rescript.symbol.SymbolTable;
import org.rescript.symbol.VarSymbol;

public class Expressions {

  private SymbolTable symbols;

  private FunctionCaller functions;

  public Expressions(SymbolTable symbols) {
    super();
    this.symbols = symbols;
    this.functions = new FunctionCaller(symbols, this);
  }

  public Value toValue(Symbol symbol) {
    if(symbol instanceof ClassSymbol) {
      ClassSymbol cs = (ClassSymbol)symbol;
      return new ObjValue(cs.getCls(), null);
    } else if(symbol instanceof VarSymbol) {
      return ((VarSymbol)symbol).getVal();
    }
    throw new ScriptException("symbol can not be converted to Value '%s'".formatted(symbol));
  }

  public SymbolTable getSymbols() {
    return symbols;
  }

  public FunctionCaller getFunctions() {
    return functions;
  }

}
