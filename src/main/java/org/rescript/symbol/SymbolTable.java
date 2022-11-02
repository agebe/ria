package org.rescript.symbol;

import java.util.List;

import org.rescript.statement.Function;
import org.rescript.symbol.java.JavaSymbols;
import org.rescript.symbol.java.RUtils;
import org.rescript.symbol.script.ScriptSymbols;
import org.rescript.value.FunctionValue;
import org.rescript.value.SymbolValue;
import org.rescript.value.Value;

public class SymbolTable {

  private ScriptSymbols scriptSymbols;

  private JavaSymbols javaSymbols;

  public SymbolTable() {
    this(new ScriptSymbols(), new JavaSymbols());
  }

  public SymbolTable(ScriptSymbols scriptSymbols, JavaSymbols javaSymbols) {
    super();
    this.scriptSymbols = scriptSymbols;
    this.javaSymbols = javaSymbols;
  }

  public ScriptSymbols getScriptSymbols() {
    return scriptSymbols;
  }

  public JavaSymbols getJavaSymbols() {
    return javaSymbols;
  }

  public Value resolveVarOrTypeOrStaticMember(String ident) {
    List<String> split = RUtils.splitTypeName(ident);
    String s0 = split.get(0);
    VarSymbol var = scriptSymbols.resolveVar(s0);
    if(var != null) {
      return valOrException(ident, javaSymbols.resolveRemaining(split.stream().skip(1).toList(), new SymbolValue(var)));
    }
    List<Function> functions = scriptSymbols.findFunctions(ident);
    if(!functions.isEmpty()) {
      return new FunctionValue(functions);
    }
    return valOrException(ident, javaSymbols.resolveTypeOrStaticMember(ident));
  }

  private Value valOrException(String ident, Value v) {
    if(v != null) {
      return v;
    } else {
      throw new SymbolNotFoundException("'%s' could not be resolved".formatted(ident));
    }
  }

}
