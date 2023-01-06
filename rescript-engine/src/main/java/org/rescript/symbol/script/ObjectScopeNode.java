package org.rescript.symbol.script;

import java.lang.reflect.Field;

import org.rescript.ScriptException;
import org.rescript.parser.Type;
import org.rescript.run.ScriptContext;
import org.rescript.symbol.ObjectVarSymbol;
import org.rescript.symbol.VarSymbol;
import org.rescript.symbol.java.RUtils;
import org.rescript.value.Value;

public class ObjectScopeNode implements ScopeNode {

  private Object o;

  private ScopeNode parent;

  private ScriptContext ctx;

  public ObjectScopeNode(Object o, ScopeNode parent, ScriptContext ctx) {
    super();
    this.o = o;
    this.parent = parent;
    this.ctx = ctx;
  }

  @Override
  public void defineVar(String name, Value val, Type type, ScriptContext ctx) {
    throw new ScriptException("define field '%s' in object scope no supported".formatted(name));
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

  private ObjectVarSymbol getField(String name) {
    Field f = RUtils.findField(o.getClass(), name);
    return f != null?new ObjectVarSymbol(o, f, ctx):null;
  }

  @Override
  public VarSymbol getVarSymbol(String name) {
    ObjectVarSymbol sym = getField(name);
    if(sym != null) {
      return sym;
    } else if(parent != null) {
      return parent.getVarSymbol(name);
    } else {
      return null;
    }
  }

  @Override
  public VarSymbol unset(String name) {
    return parent.unset(name);
  }

  @Override
  public ScopeNode getParent() {
    return parent;
  }

}
