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

import java.util.HashMap;
import java.util.Map;

import org.ria.ScriptException;
import org.ria.expression.CastOp;
import org.ria.parser.Type;
import org.ria.run.ScriptContext;
import org.ria.symbol.ScriptVarSymbol;
import org.ria.symbol.VarSymbol;
import org.ria.value.ArrayValue;
import org.ria.value.BooleanValue;
import org.ria.value.ByteValue;
import org.ria.value.CharValue;
import org.ria.value.DoubleValue;
import org.ria.value.FloatValue;
import org.ria.value.IntValue;
import org.ria.value.LongValue;
import org.ria.value.ObjValue;
import org.ria.value.ShortValue;
import org.ria.value.Value;
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

  private synchronized void define(String name, Value val, Type type, boolean initialized, ScriptContext ctx) {
    ScriptVarSymbol v = variables.putIfAbsent(name,
        new ScriptVarSymbol(name, castToNotNull(type, val, ctx), type, initialized, ctx));
    if(v != null) {
      throw new ScriptException("variable '%s' already defined".formatted(name));
    }
  }

  @Override
  public synchronized void defineVar(String name, Value val, Type type, ScriptContext ctx) {
//    if(val == null) {
//      throw new ScriptException("value is null for variable definition of '{}'".formatted(name));
//    }
    log.debug("define variable '{}', type '{}', value '{}'", name, type, val);
    define(name, val, type, true, ctx);
  }

  private Value castToNotNull(Type type, Value val, ScriptContext ctx) {
    if(type == null) {
      return val!=null?val:ObjValue.NULL;
    } else {
      return val!=null?CastOp.castTo(val, type, ctx):ObjValue.NULL;
    }
  }

  private Value defaultValue(ScriptContext ctx, Type type) {
    // FIXME add primitive arrays
    if(type == null) {
      return ObjValue.NULL;
    } else if(type.isDouble()) {
      return new DoubleValue(0);
    } else if(type.isFloat()) {
      return new FloatValue(0);
    } else if(type.isLong()) {
      return new LongValue(0);
    } else if(type.isInt()) {
      return new IntValue(0);
    } else if(type.isChar()) {
      return new CharValue((char)0);
    } else if(type.isByte()) {
      return new ByteValue((byte)0);
    } else if(type.isShort()) {
      return new ShortValue((short)0);
    } else if(type.isBoolean()) {
      return BooleanValue.FALSE;
    } else {
      Class<?> cls = type.resolve(ctx);
      if(cls == null) {
        return ObjValue.NULL;
      } else if(cls.isArray()) {
        return new ArrayValue(null, cls);
      } else {
        return new ObjValue(cls, null);
      }
    }
  }

  @Override
  public void defineVarUninitialized(String name, Type type, ScriptContext ctx) {
    log.debug("define variable '{}' uninitialized, type '{}', value '{}'", name, type);
    define(name, defaultValue(ctx, type), type, false, ctx);
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
