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

import org.junit.jupiter.api.Test;

public class TypesTest {

  @Test
  public void string() {
    new Script().run("""
        var l = List.of("1,2", "2", 'foo');
        println(l.get(0).split(",")[0]);
        """);
  }

  @Test
  public void arrayType() {
    new Script().run("""
        String[] arr = arrayof ["1,2", "2", 'foo'];
        println(arr[0].split(",")[0]);
        """);
  }

}
