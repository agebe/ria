package org.rescript.run;

import org.rescript.symbol.SymbolTable;

public class Expressions {

  private SymbolTable symbols;

  private FunctionCaller functions;

  public Expressions(SymbolTable symbols) {
    super();
    this.symbols = symbols;
    this.functions = new FunctionCaller(symbols, this);
  }

//  public Value toValue(Symbol symbol) {
//    if(symbol instanceof ClassSymbol) {
//      ClassSymbol cs = (ClassSymbol)symbol;
//      return new ClsValue(cs.getCls());
////      return new ObjValue(cs.getCls(), null);
//    } else if(symbol instanceof VarSymbol) {
//      return ((VarSymbol)symbol).getVal();
//    } else if (symbol instanceof PackageSymbol) {
//      return ((PackageSymbol)symbol).getVal();
//    } else if(symbol == null) {
//      throw new ScriptException("symbol is null");
//    } else {
//      throw new ScriptException("symbol can not be converted to Value '%s'".formatted(symbol));
//    }
//  }

  public SymbolTable getSymbols() {
    return symbols;
  }

  public FunctionCaller getFunctions() {
    return functions;
  }

}
