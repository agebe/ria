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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ImportTest {

  @Test
  public void importTest() {
    int i = new Script().evalInt("""
        import java.math.BigInteger;
        BigInteger.TEN;
        """);
    assertEquals(10, i);
  }

  @Test
  public void importAsterisk() {
    int i = new Script().evalInt("""
        import java.math.*;
        BigInteger.TEN;
        """);
    assertEquals(10, i);
  }

  @Test
  public void importStaticTest() {
    int i = new Script().evalInt("""
        import static java.math.BigInteger.TEN;
        TEN
        """);
    assertEquals(10, i);
  }

  @Test
  public void importStaticAsteriksTest() {
    int i = new Script().evalInt("""
        import static java.math.BigInteger.*;
        TEN
        """);
    assertEquals(10, i);
  }

  @Test
  public void importStaticAsteriksTest2() {
    boolean b = new Script().evalPredicate("""
        import static org.apache.commons.lang3.StringUtils.*;
        isBlank("");
        """);
    assertTrue(b);
  }

  @Test
  public void importFunctionPackage() {
    // function is a keyword in the script language and can not be used in package names.
    // to make this work also allow the import from a string
    int i = new Script().evalInt("""
        import 'org.ria.function.A';
        var a = A.test();
        """);
    assertEquals(42, i);
  }

  @Test
  public void noDefaultImports() {
    assertThrows(ScriptException.class, () ->
    new Script().run("""
        options {
          defaultImportsEnabled = false;
        }
        var l = List.of();
        """));
  }

  @Test
  @Disabled // disabled as it throws a java.awt.HeadlessException on github actions ci
  public void changeDefaultImports() {
    assertEquals("java.awt.List", new Script().run("""
        options {
          defaultImports.addFirst('java.awt.List');
        }
        typeof new List();
        """));
  }

}
