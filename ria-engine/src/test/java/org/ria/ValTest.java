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

import org.junit.jupiter.api.Test;

public class ValTest {

  @Test
  public void simpleVal() {
    new Script().run("val i;");
  }

  @Test
  public void longVal() {
    assertEquals("long", new Script().run("val i = 5l;typeof i;"));
  }

  @Test
  public void cantChange() {
    assertThrows(ScriptException.class, () -> new Script().run("val i = 42;i++;"));
  }

  @Test
  public void cantChange2() {
    assertThrows(ScriptException.class, () -> new Script().run("val i = 42;i=1;"));
  }

  @Test
  public void init1() {
    assertEquals(1, new Script().run("val i;i=1;"));
  }

  @Test
  public void init2() {
    assertThrows(ScriptException.class, () -> new Script().run("val i;i=1;i=2;"));
  }

}
