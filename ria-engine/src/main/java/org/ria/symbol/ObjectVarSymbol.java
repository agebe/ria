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

import java.lang.reflect.Field;

import org.ria.expression.CastOp;
import org.ria.parser.Type;
import org.ria.run.ScriptContext;
import org.ria.value.Value;

public class ObjectVarSymbol implements VarSymbol {

  private Object o;

  private Field f;

  private ScriptContext ctx;

  public ObjectVarSymbol(Object o, Field f, ScriptContext ctx) {
    super();
    this.o = o;
    this.f = f;
    this.ctx = ctx;
  }

  @Override
  public Value get() {
    return Value.of(f.getType(), getObjectOrNull());
  }

  @Override
  public Value inc() {
    Value v = getVal().inc();
    setVal(v);
    return getVal();
  }

  @Override
  public Value dec() {
    Value v = getVal().dec();
    setVal(v);
    return getVal();
  }

  @Override
  public String getName() {
    return f.getName();
  }

  @Override
  public Value getVal() {
    return get();
  }

  @Override
  public Object getObjectOrNull() {
    return ctx.getFirewall().checkAndGet(f, o);
  }

  @Override
  public void setVal(Value val) {
    Value v = CastOp.castTo(val, new Type(f.getType()), ctx);
    ctx.getFirewall().checkAndSet(f, o, v);
  }

  @Override
  public void set(Value v) {
    setVal(v);
  }

}
