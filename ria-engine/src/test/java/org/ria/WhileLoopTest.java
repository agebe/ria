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

import org.junit.jupiter.api.Test;

public class WhileLoopTest {

  @Test
  public void iteratorLoop() {
    int i = new Script().evalInt("""
        var iter = List.of(1,2,3).iterator();
        while(iter.hasNext()) {
          iter.next();
        }
        """);
    assertEquals(3, i);
  }

  @Test
  public void whileFalse() {
    assertEquals(1, new Script().evalInt("1;while(false) 2;"));
  }

  @Test
  public void while2() {
    assertFalse(new Script().evalPredicate("var a = true;while(a) a = false;"));
  }

  @Test
  public void whileBreak() {
    assertEquals(2, new Script().evalInt("1;while(true) {2;break;3;}"));
  }

  @Test
  public void whileContinue() {
    assertEquals(42, new Script().evalInt("1;var i = 0;var a = 42;while(i < 3) {i++;println(i);continue;a=0;}a;"));
  }

}
