package org.rescript.symbol.script;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.rescript.ScriptException;
import org.rescript.expression.CastOp;
import org.rescript.run.ScriptContext;
import org.rescript.symbol.VarSymbol;
import org.rescript.value.ObjValue;
import org.rescript.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScopeNode {

  private static final Logger log = LoggerFactory.getLogger(ScopeNode.class);

  private ScopeNode parent;

  private Map<String, VarSymbol> variables = new HashMap<>();

  public ScopeNode() {
    super();
  }

  public ScopeNode(ScopeNode parent) {
    super();
    this.parent = parent;
  }

  public void defineVar(String name, Value val, String type, ScriptContext ctx) {
//    if(val == null) {
//      throw new ScriptException("value is null for variable definition of '{}'".formatted(name));
//    }
    log.debug("define variable '{}', type '{}'", name, type);
    VarSymbol v = variables.putIfAbsent(name,
        new VarSymbol(name, castToNotNull(type, val, ctx), type, ctx));
    if(v != null) {
      throw new ScriptException("variable '%s' already defined".formatted(name));
    }
  }

  private Value castToNotNull(String type, Value val, ScriptContext ctx) {
    if(StringUtils.isBlank(type)) {
      return val!=null?val:ObjValue.NULL;
    } else {
      return val!=null?new CastOp(type, c -> val).eval(ctx):ObjValue.NULL;
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
