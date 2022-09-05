package io.github.agebe.script.run;

import io.github.agebe.script.ScriptException;
import io.github.agebe.script.parser.Expression;
import io.github.agebe.script.parser.FunctionCall;
import io.github.agebe.script.parser.Identifier;
import io.github.agebe.script.parser.StringLiteral;
import io.github.agebe.script.symbol.ClassSymbol;
import io.github.agebe.script.symbol.Symbol;
import io.github.agebe.script.symbol.SymbolTable;
import io.github.agebe.script.symbol.VarSymbol;

public class Expressions {

  private SymbolTable symbols;

  private FunctionCaller functions;

  public Expressions(SymbolTable symbols) {
    super();
    this.symbols = symbols;
    this.functions = new FunctionCaller(symbols, this);
  }

  public Value execute(Expression expr) {
    if(expr instanceof FunctionCall) {
      return functions.call((FunctionCall)expr);
    } else if(expr instanceof Identifier) {
      return toValue(symbols.resolve(((Identifier)expr).getIdent()));
    } else if(expr instanceof StringLiteral) {
      String literal = ((StringLiteral)expr).getLiteral();
      literal.intern();
      return new ObjValue(String.class, literal);
    }
    throw new ScriptException("expression execute not implemented for '%s'".formatted(expr));
  }

  private Value toValue(Symbol symbol) {
    if(symbol instanceof ClassSymbol) {
      ClassSymbol cs = (ClassSymbol)symbol;
      return new ObjValue(cs.getCls(), null);
    } else if(symbol instanceof VarSymbol) {
      return ((VarSymbol)symbol).getVal();
    }
    throw new ScriptException("symbol can not be converted to Value '%s'".formatted(symbol));
  }

}
