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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class RelationalTests {

  @Test
  public void ge() {
    assertTrue(new Script().evalPredicate("0>=0"));
  }

  @Test
  public void le() {
    assertTrue(new Script().evalPredicate("0<=0"));
  }

  @Test
  public void gt() {
    assertFalse(new Script().evalPredicate("0>0"));
  }

  @Test
  public void lt() {
    assertFalse(new Script().evalPredicate("0<0"));
  }

  @Test
  public void geFloat() {
    assertTrue(new Script().evalPredicate("0.2>=0.2"));
  }

  @Test
  public void leFloat() {
    assertTrue(new Script().evalPredicate("0.1<=0.2"));
  }

  @Test
  public void gtFloat() {
    assertFalse(new Script().evalPredicate(".3>.4"));
  }

  @Test
  public void ltFloat() {
    assertFalse(new Script().evalPredicate("-1.1<-2.5"));
  }

  @Test
  public void ltLong() {
    assertFalse(new Script().evalPredicate("-1l<-2l"));
  }

  @Test
  public void invalidConversion1() {
    assertThrows(ScriptException.class, () -> new Script().evalPredicate("true>=1"));
  }

  @Test
  public void invalidConversion2() {
    assertThrows(ScriptException.class, () -> new Script().evalPredicate("1<=true"));
  }

  @Test
  public void invalidConversion3() {
    assertThrows(ScriptException.class, () -> new Script().evalPredicate("new Object()>1"));
  }

  @Test
  public void invalidConversion4() {
    assertThrows(ScriptException.class, () -> new Script().evalPredicate("1<new Object()"));
  }

}
