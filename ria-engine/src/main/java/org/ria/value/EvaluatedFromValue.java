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

import org.ria.symbol.Symbol;

public abstract class EvaluatedFromValue implements Value, Array {

  protected abstract Value getWrapped();

  public abstract Symbol getSymbol();

  @Override
  public Class<?> type() {
    return getWrapped().type();
  }

  @Override
  public String typeOf() {
    return getWrapped().typeOf();
  }

  @Override
  public Object val() {
    return getWrapped().val();
  }

  @Override
  public boolean isNull() {
    return getWrapped().isNull();
  }

  @Override
  public boolean isNotNull() {
    // TODO Auto-generated method stub
    return getWrapped().isNotNull();
  }

  @Override
  public boolean isBoolean() {
    return getWrapped().isBoolean();
  }

  @Override
  public boolean isNumber() {
    return getWrapped().isNumber();
  }

  @Override
  public boolean isDouble() {
    return getWrapped().isDouble();
  }

  @Override
  public boolean isFloat() {
    return getWrapped().isFloat();
  }

  @Override
  public boolean isLong() {
    return getWrapped().isLong();
  }

  @Override
  public boolean isInteger() {
    return getWrapped().isInteger();
  }

  @Override
  public boolean isString() {
    return getWrapped().isString();
  }

  @Override
  public boolean isPrimitive() {
    return getWrapped().isPrimitive();
  }

  @Override
  public boolean equalsOp(Value other) {
    return getWrapped().equalsOp(other);
  }

  @Override
  public String getText() {
    return getWrapped().getText();
  }

  @Override
  public boolean toBoolean() {
    return getWrapped().toBoolean();
  }

  @Override
  public double toDouble() {
    return getWrapped().toDouble();
  }

  @Override
  public float toFloat() {
    return getWrapped().toFloat();
  }

  @Override
  public int toInt() {
    return getWrapped().toInt();
  }

  @Override
  public long toLong() {
    return getWrapped().toLong();
  }

  @Override
  public Value unbox() {
    return getWrapped().unbox();
  }

  @Override
  public boolean isChar() {
    return getWrapped().isChar();
  }

  @Override
  public boolean isFunction() {
    return getWrapped().isFunction();
  }

  @Override
  public char toChar() {
    return getWrapped().toChar();
  }

  @Override
  public Value inc() {
    return getWrapped().inc();
  }

  @Override
  public Value dec() {
    return getWrapped().dec();
  }

  @Override
  public FunctionValue toFunctionValue() {
    return getWrapped().toFunctionValue();
  }

  @Override
  public boolean isMethod() {
    return getWrapped().isMethod();
  }

  @Override
  public MethodValue toMethodValue() {
    return getWrapped().toMethodValue();
  }

  @Override
  public boolean isConstructor() {
    return getWrapped().isConstructor();
  }

  @Override
  public ConstructorValue toConstructorValue() {
    return getWrapped().toConstructorValue();
  }

  @Override
  public boolean isArray() {
    return getWrapped().isArray();
  }

  @Override
  public Array toArray() {
    return getWrapped().toArray();
  }

  @Override
  public boolean isByte() {
    return getWrapped().isByte();
  }

  @Override
  public byte toByte() {
    return getWrapped().toByte();
  }

  @Override
  public boolean isShort() {
    return getWrapped().isShort();
  }

  @Override
  public short toShort() {
    return getWrapped().toShort();
  }

  @Override
  public Value get(int index) {
    return ((Array)getWrapped()).get(index);
  }

  @Override
  public int length() {
    return ((Array)getWrapped()).length();
  }

}
