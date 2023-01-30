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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class UnaryOpTest {

  @Test
  public void unaryPlus() {
    assertEquals(1, new Script().evalInt("+1"));
  }

  @Test
  public void unaryPlus2() {
    assertThrows(ScriptException.class, () -> new Script().run("+true"));
  }

  @Test
  public void unaryMinus() {
    assertEquals(-1, new Script().evalInt("-1"));
  }

  @Test
  public void unaryMinus2() {
    assertThrows(ScriptException.class, () -> new Script().run("-true"));
  }

  @Test
  public void unaryLogicalNot() {
    assertTrue(new Script().evalPredicate("!false"));
  }

  @Test
  public void unaryLogicalNot2() {
    // operator precedence
    assertThrows(ScriptException.class, () -> new Script().evalPredicate("!1>2)"));
  }

  @Test
  public void unaryLogicalNot3() {
    assertTrue(new Script().evalPredicate("!(1>2)"));
  }

  @Test
  public void unaryLogicalNot4() {
    assertFalse(new Script().evalPredicate("!true"));
  }

}
