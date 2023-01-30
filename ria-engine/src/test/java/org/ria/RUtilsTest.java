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

import org.junit.jupiter.api.Test;
import org.ria.symbol.java.RUtils;

public class RUtilsTest {

  public static class StaticInner {
    
  }

  public class Inner {
    
  }

  @Test
  public void findClass() {
    Class<?> cls = RUtils.findClass("java.lang.Object", this.getClass().getClassLoader());
    assertEquals(Object.class, cls);
  }

  @Test
  public void findClass2() {
    Class<?> cls = RUtils.findClass(
        this.getClass().getPackageName(),
        "RUtilsTest.StaticInner",
        this.getClass().getClassLoader());
    assertEquals(StaticInner.class, cls);
  }

  @Test
  public void findClass3() {
    Class<?> cls = RUtils.findClass(
        this.getClass().getPackageName(),
        "RUtilsTest.Inner",
        this.getClass().getClassLoader());
    assertEquals(Inner.class, cls);
  }


}
