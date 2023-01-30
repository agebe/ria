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

import java.util.List;

import org.junit.jupiter.api.Test;

public class ClassLiteralTest {

  @Test
  public void simple() {
    assertEquals(String.class, new Script().run("String.class"));
  }

  @Test
  public void string() {
    assertEquals(String.class, new Script().run("java.lang.String.class"));
  }

  @Test
  public void list() {
    assertEquals(List.class, new Script().run("List.class"));
  }

  @Test
  public void listName() {
    assertEquals("java.util.List", new Script().run("List.class.getName()"));
  }

  @Test
  public void floatDotClass() {
    assertEquals(float.class, new Script().run("float.class"));
  }

  @Test
  public void floatDotClassName() {
    assertEquals("float", new Script().run("float.class.getName()"));
  }

  @Test
  public void intArrayDotClassName() {
    assertEquals("[I", new Script().run("int[].class.getName()"));
  }

  @Test
  public void intArray2DotClassName() {
    assertEquals("[[I", new Script().run("int[][].class.getName()"));
  }

}
