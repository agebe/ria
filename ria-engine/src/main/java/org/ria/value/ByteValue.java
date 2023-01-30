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

public class ByteValue implements Value {

  private final byte val;

  public ByteValue(byte val) {
    super();
    this.val = val;
  }

  public ByteValue(Object o) {
    this(((Number)o).byteValue());
  }

  @Override
  public Class<?> type() {
    return byte.class;
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
  public boolean equalsOp(Value other) {
    return this.val == other.toByte();
  }

  @Override
  public boolean isNumber() {
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
  public char toChar() {
    return (char)val;
  }

  @Override
  public byte toByte() {
    return val;
  }

  @Override
  public short toShort() {
    return val;
  }

  @Override
  public boolean isByte() {
    return true;
  }

  @Override
  public boolean isShort() {
    return false;
  }

  @Override
  public Value inc() {
    return new ByteValue(this.val+1);
  }

  @Override
  public Value dec() {
    return new ByteValue(this.val-1);
  }

}
