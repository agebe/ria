/*
 * Copyright 2023 Andre Gebers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ria.symbol.script;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.ria.ScriptException;
import org.ria.parser.Type;
import org.ria.run.ScriptContext;
import org.ria.symbol.ObjectMethodSymbol;
import org.ria.symbol.ObjectVarSymbol;
import org.ria.symbol.VarSymbol;
import org.ria.symbol.java.RUtils;
import org.ria.value.MethodValue;
import org.ria.value.Value;

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
  public void defineVarUninitialized(String name, Type type, ScriptContext ctx) {
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

  @Override
  public VarSymbol getFunctionSymbol(String name) {
    List<Method> methods = RUtils.findAccessibleMethods(o.getClass(), o, name);
    return !methods.isEmpty()?new ObjectMethodSymbol(new MethodValue(o.getClass(), o, name)):parent.getFunctionSymbol(name);
  }

}
