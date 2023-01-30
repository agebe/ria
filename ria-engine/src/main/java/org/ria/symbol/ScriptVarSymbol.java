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
package org.ria.symbol;

import org.ria.ScriptException;
import org.ria.expression.CastOp;
import org.ria.parser.Type;
import org.ria.run.ScriptContext;
import org.ria.value.Value;

public class ScriptVarSymbol implements VarSymbol {

  private String name;

  private Value val;

  private Type type;

  private boolean initialized;

  private ScriptContext ctx;

  public ScriptVarSymbol(String name, Value val, Type type, boolean initialized, ScriptContext ctx) {
    super();
    this.name = name;
    this.val = val;
    this.type = type;
    this.initialized = initialized;
    this.ctx = ctx;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Value getVal() {
    return val;
  }

  @Override
  public Object getObjectOrNull() {
    return val!=null?val.val():null;
  }

  private Value setValue(Value val) {
    if(type != null) {
      if(initialized && type.isVal()) {
        throw new ScriptException("val variable '%s' can not change".formatted(name));
      } else {
        this.val = CastOp.castTo(val, type, ctx);
      }
    } else {
      this.val = val;
    }
    initialized = true;
    return this.val;
  }

  @Override
  public void setVal(Value val) {
    setValue(val);
  }

  @Override
  public Value inc() {
    return setValue(val.inc());
  }

  @Override
  public Value dec() {
    return setValue(val.dec());
  }

  @Override
  public Value get() {
    return getVal();
  }

  @Override
  public String toString() {
    return "VarSymbol [name=" + name + ", val=" + val + "]";
  }

  @Override
  public void set(Value v) {
    setValue(v);
  }

}
