package org.rescript.symbol.script;

import java.util.HashMap;
import java.util.Map;

import org.rescript.ScriptException;
import org.rescript.expression.CastOp;
import org.rescript.parser.Type;
import org.rescript.run.ScriptContext;
import org.rescript.symbol.ScriptVarSymbol;
import org.rescript.symbol.VarSymbol;
import org.rescript.value.ObjValue;
import org.rescript.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptScopeNode implements ScopeNode {

  private static final Logger log = LoggerFactory.getLogger(ScriptScopeNode.class);

  private ScopeNode parent;

  private Map<String, ScriptVarSymbol> variables = new HashMap<>();

  public ScriptScopeNode() {
    super();
  }

  public ScriptScopeNode(ScopeNode parent) {
    super();
    this.parent = parent;
  }

  @Override
  public synchronized void defineVar(String name, Value val, Type type, ScriptContext ctx) {
//    if(val == null) {
//      throw new ScriptException("value is null for variable definition of '{}'".formatted(name));
//    }
    log.debug("define variable '{}', type '{}', value '{}'", name, type, val);
    ScriptVarSymbol v = variables.putIfAbsent(name,
        new ScriptVarSymbol(name, castToNotNull(type, val, ctx), type, ctx));
    if(v != null) {
      throw new ScriptException("variable '%s' already defined".formatted(name));
    }
  }

  private Value castToNotNull(Type type, Value val, ScriptContext ctx) {
    if(type == null) {
      return val!=null?val:ObjValue.NULL;
    } else {
      return val!=null?CastOp.castTo(val, type, ctx):ObjValue.NULL;
    }
  }

  @Override
  public void assignVar(String name, Value val) {
    VarSymbol v = getVarSymbol(name);
    if(v != null) {
      v.setVal(val);
    } else {
      throw new ScriptException("variable '%s' not defined".formatted(name));
    }
  }

  @Override
  public synchronized VarSymbol getVarSymbol(String name) {
    ScriptVarSymbol sym = variables.get(name);
    if(sym != null) {
      return sym;
    } else if(parent != null) {
      return parent.getVarSymbol(name);
    } else {
      return null;
    }
  }

  @Override
  public ScopeNode getParent() {
    return parent;
  }

  @Override
  public synchronized VarSymbol unset(String name) {
    ScriptVarSymbol sym = variables.get(name);
    if(sym != null) {
      variables.remove(name);
      return sym;
    } else if(parent != null) {
      return parent.unset(name);
    } else {
      return null;
    }
  }

  @Override
  public VarSymbol getFunctionSymbol(String name) {
    // currently only used in ObjectScopeNode
    // but might want to move the script function finding to here
    return parent!=null?parent.getFunctionSymbol(name):null;
  }

}
