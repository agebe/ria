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

public class LongValue implements Value {

  private final long val;

  public LongValue(long val) {
    super();
    this.val = val;
  }

  public LongValue(Object o) {
    super();
    val = ((Number)o).longValue();
  }

  @Override
  public Class<?> type() {
    return long.class;
  }

  @Override
  public Object val() {
    return val;
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
    return (int)val;
  }

  @Override
  public long toLong() {
    return val;
  }

  @Override
  public boolean isNumber() {
    return true;
  }

  @Override
  public boolean isLong() {
    return true;
  }

  @Override
  public String getText() {
    return Long.toString(val);
  }

  @Override
  public String toString() {
    return getText();
  }

  @Override
  public boolean isPrimitive() {
    return true;
  }

  @Override
  public boolean equalsOp(Value other) {
    return this.val == other.toLong();
  }

  @Override
  public Value inc() {
    return new LongValue(this.val+1);
  }

  @Override
  public Value dec() {
    return new LongValue(this.val-1);
  }

  @Override
  public char toChar() {
    return (char)val;
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
