package org.rescript.symbol.script;

import org.rescript.ScriptException;
import org.rescript.statement.Statement;
import org.rescript.symbol.VarSymbol;
import org.rescript.value.Value;

public class ScriptSymbols {

  private Statement entryPoint;

  private ScopeNode root;

  private ScopeNode current;

  public ScriptSymbols() {
    super();
    root = new ScopeNode();
    current = root;
  }

  public void defineVar(String name, Value val) {
    current.defineVar(name, val);
  }

  public void assignVar(String name, Value val) {
    current.assignVar(name, val);
  }

  public void defineOrAssignVarRoot(String name, Value val) {
    VarSymbol s = root.getVarSymbol(name);
    if(s != null) {
      s.setVal(val);
    } else {
      root.defineVar(name, val);
    }
  }

  public Value getVariable(String name) {
    return current.getVariable(name);
  }

  public Statement getEntryPoint() {
    return entryPoint;
  }

  public void setEntryPoint(Statement entryPoint) {
    this.entryPoint = entryPoint;
  }

  public VarSymbol resolveVar(String ident) {
    return current.getVarSymbol(ident);
  }

  public void enterScope() {
    current = new ScopeNode(current);
  }

  public void exitScope() {
    if(current.getParent() == null) {
      throw new ScriptException("can't exit root scope");
    } else {
      current = current.getParent();
    }
  }

}
