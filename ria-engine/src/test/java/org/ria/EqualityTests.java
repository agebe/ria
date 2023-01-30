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

public class EqualityTests {

  @Test
  public void equalsIntegralTrue() {
    assertTrue(new Script().evalPredicate("0==0"));
  }

  @Test
  public void equalsIntegralFalse() {
    assertFalse(new Script().evalPredicate("0==1"));
  }

  @Test
  public void notEqualsIntegralFalse() {
    assertFalse(new Script().evalPredicate("0!=0"));
  }

  @Test
  public void notEqualsIntegralTrue() {
    assertTrue(new Script().evalPredicate("0!=1"));
  }

  @Test
  public void floatPoint() {
    assertTrue(new Script().evalPredicate(".5==.5"));
  }

  @Test
  public void doubleAndFloat() {
    assertTrue(new Script().evalPredicate(".5d==.5f"));
  }

  @Test
  public void longAndInt() {
    assertTrue(new Script().evalPredicate("5l==5"));
  }

  @Test
  public void stringLiteral() {
    // String literals are interned
    assertTrue(new Script().evalPredicate("\"123\"==\"123\""));
  }

  @Test
  public void stringLiteralCase() {
    // String literals are interned
    assertFalse(new Script().evalPredicate("\"abc\"==\"ABC\""));
  }

  @Test
  public void differentObjectReference() {
    assertTrue(new Script().evalPredicate("new Object()!=new Object()"));
  }

  @Test
  public void sameObjectReference() {
    assertTrue(new Script().evalPredicate("var a = new Object();a==a;"));
  }

  @Test
  public void emptyString() {
    assertTrue(new Script().evalPredicate("\"\"==\"\""));
  }

  @Test
  public void equalsBoolTrue() {
    assertTrue(new Script().evalPredicate("true==true"));
  }

  @Test
  public void equalsBoolFalse() {
    assertFalse(new Script().evalPredicate("true==false"));
  }

  @Test
  public void notEqualsBoolTrue() {
    assertTrue(new Script().evalPredicate("true!=false"));
  }

  @Test
  public void notEqualsBoolFalse() {
    assertFalse(new Script().evalPredicate("true!=true"));
  }

  @Test
  public void invalidConversion1() {
    assertThrows(ScriptException.class, () -> new Script().evalPredicate("true==1"));
  }

  @Test
  public void invalidConversion2() {
    assertThrows(ScriptException.class, () -> new Script().evalPredicate("1==true"));
  }

  @Test
  public void invalidConversion3() {
    assertThrows(ScriptException.class, () -> new Script().evalPredicate("new Object()==1"));
  }

  @Test
  public void invalidConversion4() {
    assertThrows(ClassCastException.class, () -> new Script().evalPredicate("1==new Object()"));
  }

}
