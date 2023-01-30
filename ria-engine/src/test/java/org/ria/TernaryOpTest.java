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

public class TernaryOpTest {

  @Test
  public void ternaryTrue() {
    assertEquals(1, new Script().evalInt("true?1:2"));
  }

  @Test
  public void ternaryFalse() {
    assertEquals(2, new Script().evalInt("false?1:2"));
  }

  @Test
  public void ternaryFail() {
    assertThrows(ScriptException.class, () -> new Script().evalInt("0?1:2"));
  }

}
