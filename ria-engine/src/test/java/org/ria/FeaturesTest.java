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

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class FeaturesTest {

  @Test
  public void disabledDependenciesTest() {
    assertThrows(ScriptException.class, () -> new Script()
    .setFeatures(new Features().setDependenciesEnabled(false))
    .setQuiet(true)
    .run("""
        dependencies {
          'org.apache.commons:commons-lang3:3.12.0'
        }
        StringUtils.isBlank('123');
        """));
  }

  @Test
  public void disabledJavaSourceTest() {
    assertThrows(ScriptException.class, () -> new Script()
    .setFeatures(new Features().setJavaSourceEnabled(false))
    .setQuiet(true)
    .run("""
        javasrc '''
          public class A {}
        ''';
        new A();
        """));
  }

  @Test
  public void disabledLoopsTest() {
    assertThrows(ScriptException.class, () -> new Script()
    .setFeatures(new Features().setLoopsEnabled(false))
    .run("""
        for(var s : List.of('a','b','c')) {
          println(s);
        }
        """));
  }

}