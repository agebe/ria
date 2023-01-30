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
package org.ria.symbol.java;

import java.lang.reflect.Field;

import org.ria.run.ScriptContext;
import org.ria.symbol.Symbol;
import org.ria.value.Value;

public class FieldSymbol implements Symbol {

  private Field field;

  private Object owner;

  private ScriptContext ctx;

  public FieldSymbol(Field field, Object owner, ScriptContext ctx) {
    super();
    this.field = field;
    this.owner = owner;
    this.ctx = ctx;
  }

  @Override
  public Value get() {
    return Value.of(field.getType(), ctx.getFirewall().checkAndGet(field, owner));
  }

  @Override
  public Value inc() {
    Value v = get().inc();
    set(v);
    return get();
  }

  @Override
  public Value dec() {
    Value v = get().dec();
    set(v);
    return get();
  }

  @Override
  public void set(Value v) {
    ctx.getFirewall().checkAndSet(field, owner, v);
  }

}
