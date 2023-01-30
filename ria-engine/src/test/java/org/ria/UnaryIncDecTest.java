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

public class UnaryIncDecTest {

  public static int COUNTER = 0;

  public static class IncDecTest {
    public int counter;
  }

  @Test
  public void postInc() {
    assertEquals(1, new Script().evalInt("var i = 1;i++;"));
  }

  @Test
  public void postIncDouble() {
    assertEquals(1.5, new Script().evalDouble("var i = 1.5d;i++;"));
  }


  @Test
  public void postDec() {
    assertEquals(1, new Script().evalInt("var i = 1;i--;"));
  }

  @Test
  public void postIncFail() {
    assertThrows(ScriptException.class, () ->new Script().evalInt("0++"));
  }

  @Test
  public void testCounter() {
    new Script().run("%s.COUNTER++".formatted(this.getClass().getName()));
    assertEquals(1, COUNTER);
  }

  @Test
  public void testMemberPost() {
    IncDecTest o = new IncDecTest();
    Script script = new Script();
    script.setVariable("o", o);
    script.run("org.junit.jupiter.api.Assertions.assertEquals(0, o.counter++)");
    assertEquals(o, script.getVariable("o"));
    assertEquals(1, o.counter);
    script.run("org.junit.jupiter.api.Assertions.assertEquals(1, o.counter--)");
    assertEquals(0, o.counter);
  }

  @Test
  public void preInc() {
    assertEquals(2, new Script().evalInt("var i = 1;++i;"));
  }

  @Test
  public void preIncDouble() {
    assertEquals(2.5, new Script().evalDouble("var i = 1.5d;++i;"));
  }


  @Test
  public void preDec() {
    assertEquals(0, new Script().evalInt("var i = 1;--i;"));
  }

  @Test
  public void preIncFail() {
    assertThrows(ScriptException.class, () ->new Script().evalInt("++0"));
  }

  @Test
  public void preIncFail2() {
    assertThrows(ScriptException.class, () ->new Script().evalInt("++\"123\""));
  }


  @Test
  public void testMemberPre() {
    IncDecTest o = new IncDecTest();
    Script script = new Script();
    script.setVariable("o", o);
    script.run("org.junit.jupiter.api.Assertions.assertEquals(1, ++o.counter)");
    assertEquals(o, script.getVariable("o"));
    assertEquals(1, o.counter);
    script.run("org.junit.jupiter.api.Assertions.assertEquals(0, --o.counter)");
    assertEquals(0, o.counter);
  }

}
