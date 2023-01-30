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

import org.junit.jupiter.api.Test;

public class ShortTest {

  @Test
  public void simple() {
    new Script().run("short b = 1;");
  }

  @Test
  public void typeof() {
    assertEquals("short", new Script().run("short b = 1; typeof b;"));
  }

  @Test
  public void add() {
    assertEquals(2, new Script().evalShort("short b = 1; b+b;"));
  }

  @Test
  public void array() {
    assertEquals(2, new Script().run("var b = new short[2];b.length;"));
  }

  @Test
  public void array2() {
    assertEquals(2, new Script().run("short[] b = new short[2];b.length;"));
  }

  @Test
  public void arrayInit() {
    assertArrayEquals(new short[] {-1,1,2}, (short[])new Script().run("new short[] {-1,1,2}"));
  }

  @Test
  public void arrayInit2() {
    assertArrayEquals(new int[] {-1,1,2}, (int[])new Script().run("new int[] {-1,1,2}"));
  }

}
