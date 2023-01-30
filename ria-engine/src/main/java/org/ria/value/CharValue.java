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

public class CharValue implements Value {

  private final char val;

  public CharValue(char val) {
    super();
    this.val = val;
  }

  public CharValue(Object o) {
    super();
    this.val = (Character)o;
  }

  @Override
  public Class<?> type() {
    return char.class;
  }

  @Override
  public Object val() {
    return val;
  }

  @Override
  public boolean isPrimitive() {
    return true;
  }

  @Override
  public double toDouble() {
    return val;
  }

  @Override
  public float toFloat() {
    return val;
  }

  @Override
  public int toInt() {
    return val;
  }

  @Override
  public long toLong() {
    return val;
  }

  @Override
  public boolean isNumber() {
    return false;
  }

  @Override
  public String getText() {
    return Character.toString(val);
  }

  @Override
  public String toString() {
    return getText();
  }

  @Override
  public boolean equalsOp(Value other) {
    return this.val == other.toInt();
  }

  @Override
  public Value inc() {
    char c = this.val;
    c+=1;
    return new CharValue(c);
  }

  @Override
  public Value dec() {
    char c = this.val;
    c-=1;
    return new CharValue(c);
  }

  @Override
  public char toChar() {
    return val;
  }

  @Override
  public boolean isChar() {
    return true;
  }

  @Override
  public byte toByte() {
    return (byte)val;
  }

  @Override
  public short toShort() {
    return (short)val;
  }

}
