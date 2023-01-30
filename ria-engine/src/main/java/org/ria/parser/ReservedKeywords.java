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
package org.ria.parser;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReservedKeywords {

  private static Set<String> reservedKeywords = Stream.of(
      "_",
      "abstract",
      "assert",
      "const",
      "enum",
      "extends",
      "final",
      "goto",
      "implements",
      "interface",
      "native",
      "package",
      "private",
      "protected",
      "public",
      "record",
      "strictfp",
      "super",
      "synchronized",
      "this",
      "throws",
      "transient",
      "volatile"
      ).collect(Collectors.toSet());

  public static boolean isReservedKeyword(String s) {
    return reservedKeywords.contains(s);
  }

  public static Stream<String> reservedKeywords() {
    return reservedKeywords
        .stream()
        .sorted();
  }

}
