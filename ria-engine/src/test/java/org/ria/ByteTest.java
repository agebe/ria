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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ByteTest {

  @Test
  public void simple() {
    new Script().run("byte b = 1;");
  }

  @Test
  public void typeof() {
    assertEquals("byte", new Script().run("byte b = 1; typeof b;"));
  }

  @Test
  public void add() {
    assertEquals(2, new Script().evalByte("byte b = 1; b+b;"));
  }

  @Test
  public void unaryMinus() {
    assertEquals(-1, new Script().evalByte("byte b = 1; -b;"));
  }

  @Test
  public void preInc() {
    assertEquals(2, new Script().evalByte("byte b = 1; ++b;"));
  }

  @Test
  public void postInc() {
    Script s = new Script();
    assertEquals(1, s.evalByte("byte b = 1; b++;"));
    assertEquals((byte)2, s.getVariable("b"));
  }

  @Test
  public void gt() {
    assertTrue(new Script().evalPredicate("byte b = 2; b > 1;"));
  }

  @Test
  public void gtFalse() {
    assertFalse(new Script().evalPredicate("byte b = 2; b > (byte)2;"));
  }

  @Test
  public void overflow() {
    assertEquals(-128, new Script().evalByte("byte b = 127; ++b;"));
  }

  @Test
  public void array() {
    assertEquals(2, new Script().run("var b = new byte[2];b.length;"));
  }

  @Test
  public void arrayInit() {
    assertArrayEquals(new byte[] {-1,1,2}, (byte[])new Script().run("new byte[] {-1,1,2}"));
  }

  @Test
  public void array2() {
    assertEquals(2, new Script().run("byte[] b = new byte[2];b.length;"));
  }

}
