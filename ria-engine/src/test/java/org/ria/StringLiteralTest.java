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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class StringLiteralTest {

  @Test
  public void simple() {
    assertEquals("a", new Script().run("\"a\""));
  }

  @Test
  public void equals() {
    // this should work as string literals are interned
    assertTrue(new Script().evalPredicate("\"a\" == \"a\""));
  }

  @Test
  public void escapeSequence() {
    assertEquals("\n", new Script().run("\"\\n\""));
  }

  @Test
  public void print() {
    new Script().run("println(\"a\\n\")");
  }

  @Test
  public void omega() {
    assertEquals("Ω", new Script().run("\"\\u03A9\""));
  }

  @Test
  public void stringConcat() {
    assertEquals("ab", new Script().run("""
        "a"+"b"
        """));
  }

}
