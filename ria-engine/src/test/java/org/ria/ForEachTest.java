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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class ForEachTest {

  @Test
  public void simple() {
    new Script().run("for(i : new java.util.ArrayList()) println(i);");
  }

  @Test
  public void iterate() {
    new Script().run("for(i : java.util.List.of(1,2,3)) println(typeof i);");
  }

  @Test
  public void iterateWithType() {
    new Script().run("for(int i : java.util.List.of(1,2,3)) println(typeof i);");
  }

  @Test
  public void iterateWithWrongType() {
    assertThrows(ScriptException.class,
        () -> new Script().run("for(boolean i : java.util.List.of(1,2,3)) println(i);"));
  }

  @Test
  public void wrongIterable() {
    assertThrows(ScriptException.class, () -> new Script().run("for(i : \"1 2 3\") println(i);"));
  }

  @Test
  public void breakContinue() {
    Script s = new Script();
    s.setVariable("l", new ArrayList<Integer>());
    s.run("""
        for(var i : java.util.List.of(1,2,3,4,5,6,7,8,9)) {
          if(i == 2) {
            continue;
          } else if(i > 4) {
            break;
          } else {
            l.add(i);
          }
        }
        """);
    assertEquals(List.of(1,3,4), s.getVariable("l"));
  }

  @Test
  public void breakContinueOverListLiteral() {
    Script s = new Script();
    s.setVariable("l", new ArrayList<Integer>());
    s.run("""
        for(var i : [1,2,3,4,5,6,7,8,9]) {
          if(i == 2) {
            continue;
          } else if(i > 4) {
            break;
          } else {
            l.add(i);
          }
        }
        """);
    assertEquals(List.of(1,3,4), s.getVariable("l"));
  }

  @Test
  public void breakContinueOverArray() {
    Script s = new Script();
    s.setVariable("l", new ArrayList<Integer>());
    s.run("""
        for(var i : arrayof [1,2,3,4,5,6,7,8,9]) {
          if(i == 2) {
            continue;
          } else if(i > 4) {
            break;
          } else {
            l.add(i);
          }
        }
        """);
    assertEquals(List.of(1,3,4), s.getVariable("l"));
  }

}
