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
package org.ria.value;

import org.ria.ScriptException;

public class VoidValue implements Value {

  public static final VoidValue VOID = new VoidValue();

  private VoidValue() {
    super();
  }

  @Override
  public Class<?> type() {
    return Void.class;
  }

  @Override
  public Object val() {
    return null;
  }

  @Override
  public boolean isNull() {
    return true;
  }

  @Override
  public boolean toBoolean() {
    throw new ScriptException("void can't be cast to boolean");
  }

  @Override
  public double toDouble() {
    throw new ScriptException("void can't be cast to double");
  }

  @Override
  public float toFloat() {
    throw new ScriptException("void can't be cast to float");
  }

  @Override
  public int toInt() {
    throw new ScriptException("void can't be cast to int");
  }

  @Override
  public long toLong() {
    throw new ScriptException("void can't be cast to long");
  }

  @Override
  public boolean isPrimitive() {
    return true;
  }

  @Override
  public boolean equalsOp(Value other) {
    // there is only 1 void value
    return this == other;
  }

  @Override
  public Value unbox() {
    throw new ScriptException("can't unbox void");
  }

}
