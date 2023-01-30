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
package org.ria;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class BitOpTest {

  @Test
  public void simpleAnd() {
    assertEquals(1, new Script().evalInt("9 & 5"));
  }

  @Test
  public void simpleOr() {
    assertEquals(13, new Script().evalInt("9 | 5"));
  }

  @Test
  public void simpleXor() {
    assertEquals(3, new Script().evalInt("6 ^ 5"));
  }

  @Test
  public void simpleNot() {
    assertEquals(-7, new Script().evalInt("~6"));
  }

  @Test
  public void leftShift() {
    assertEquals(10, new Script().evalInt("5 << 1"));
  }

  @Test
  public void rightShift() {
    assertEquals(5, new Script().evalInt("10 >> 1"));
  }

  @Test
  public void unsginedRightShift() {
    assertEquals(2, new Script().evalInt("10 >>> 2"));
  }

  @Test
  public void rightShift2() {
    assertEquals(-5, new Script().evalInt("-10 >> 1"));
  }

  @Test
  public void unsginedRightShift2() {
    assertEquals(3, new Script().evalInt("-10 >>> 30"));
  }

}
