package org.rescript.symbol.script;

import org.rescript.ScriptException;
import org.rescript.statement.Function;
import org.rescript.symbol.VarSymbol;
import org.rescript.value.Value;

public class ScriptSymbols {

  private Function main;

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

  public VarSymbol unsetRoot(String name) {
    return root.unset(name);
  }

  public Value getVariable(String name) {
    return current.getVariable(name);
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

  public Function getMain() {
    return main;
  }

  public void setMain(Function main) {
    this.main = main;
  }

}
