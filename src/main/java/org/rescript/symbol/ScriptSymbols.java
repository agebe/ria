package org.rescript.symbol;

import java.util.HashMap;
import java.util.Map;

import org.rescript.ScriptException;
import org.rescript.statement.Statement;
import org.rescript.value.Value;
import org.rescript.value.VoidValue;

public class ScriptSymbols {

  private Map<String, VarSymbol> variables = new HashMap<>();

  private Statement entryPoint;

  public void defineVar(String name, Value val) {
    VarSymbol v = variables.putIfAbsent(name, new VarSymbol(name, val!=null?val:new VoidValue()));
    if(v != null) {
      throw new ScriptException("variable '%s' already defined".formatted(name));
    }
  }

  public void assignVar(String name, Value val) {
    VarSymbol v = variables.get(name);
    if(v != null) {
      v.setVal(val);
    } else {
      defineVar(name, val);
    }
  }

  public Value getVariable(String name) {
    VarSymbol sym = variables.get(name);
    return sym!=null?sym.getVal():null;
  }

  public Statement getEntryPoint() {
    return entryPoint;
  }

  public void setEntryPoint(Statement entryPoint) {
    this.entryPoint = entryPoint;
  }

  public VarSymbol resolveVar(String ident) {
    // check variable
    return variables.get(ident);
  }

}
