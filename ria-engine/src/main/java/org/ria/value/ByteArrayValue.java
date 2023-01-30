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

import java.util.Arrays;

public class ByteArrayValue extends AbstractArrayValue {

  private byte[] array;

  public ByteArrayValue(byte[] array) {
    super();
    this.array = array;
  }

  @Override
  public Class<?> type() {
    return byte[].class;
  }

  @Override
  public Object val() {
    return array;
  }

  @Override
  public boolean equalsOp(Value other) {
    return other instanceof ByteArrayValue a?Arrays.equals(this.array, a.array):false;
  }

  @Override
  public Value get(int index) {
    return new ByteValue(array[index]);
  }

  @Override
  public int length() {
    return array.length;
  }

}
