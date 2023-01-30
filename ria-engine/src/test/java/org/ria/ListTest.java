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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ListTest {

  @Test
  public void literal() {
    assertEquals(List.of(1,2,3), new Script().run("[1,2,3]"));
  }

  @Test
  public void listOf() {
    assertEquals(List.of(1,2,3), new Script().run("java.util.List.of(1,2,3)"));
  }

  @Test
  public void listIndex() {
    assertEquals(2, new Script().run("java.util.List.of(1,2,3)[1]"));
  }

  @Test
  public void listLiteralIndex() {
    assertEquals(2, new Script().run("[1,2,3][1]"));
  }

  @Test
  public void listofLists() {
    assertEquals(List.of(List.of(42,43), List.of(1,2,3)), new Script().run("[[42,43],[1,2,3]]"));
  }

  @Test
  public void listLiteralIndexOutOfBounds() {
    assertThrows(IndexOutOfBoundsException.class, () -> new Script().run("[1,2,3][3]"));
  }

}
