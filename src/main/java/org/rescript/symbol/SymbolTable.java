package org.rescript.symbol;

import java.util.List;

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
      return javaSymbols.resolveRemaining(split.stream().skip(1).toList(), new SymbolValue(var));
    }
    return javaSymbols.resolveTypeOrStaticMember(ident);
  }
}
