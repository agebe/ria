package org.rescript.symbol.script;

import java.util.HashMap;
import java.util.Map;

import org.rescript.ScriptException;
import org.rescript.symbol.VarSymbol;
import org.rescript.value.Value;
import org.rescript.value.VoidValue;

public class ScopeNode {

  private ScopeNode parent;

  private Map<String, VarSymbol> variables = new HashMap<>();

  public ScopeNode() {
    super();
  }

  public ScopeNode(ScopeNode parent) {
    super();
    this.parent = parent;
  }

  public void defineVar(String name, Value val) {
    VarSymbol v = variables.putIfAbsent(name, new VarSymbol(name, val!=null?val:new VoidValue()));
    if(v != null) {
      throw new ScriptException("variable '%s' already defined".formatted(name));
    }
  }

  public void assignVar(String name, Value val) {
    VarSymbol v = getVarSymbol(name);
    if(v != null) {
      v.setVal(val);
    } else {
      throw new ScriptException("variable '%s' not defined".formatted(name));
    }
  }

  public Value getVariable(String name) {
    VarSymbol sym = getVarSymbol(name);
    return sym!=null?sym.getVal():null;
  }

  public VarSymbol getVarSymbol(String name) {
    VarSymbol sym = variables.get(name);
    if(sym != null) {
      return sym;
    } else if(parent != null) {
      return parent.getVarSymbol(name);
    } else {
      return null;
    }
  }

  public ScopeNode getParent() {
    return parent;
  }

  public VarSymbol unset(String name) {
    VarSymbol sym = variables.get(name);
    if(sym != null) {
      variables.remove(name);
      return sym;
    } else if(parent != null) {
      return parent.unset(name);
    } else {
      return null;
    }
  }

}
