package org.rescript.parser;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReservedKeywords {

  private static Set<String> reservedKeywords = Stream.of(
      "_",
      "abstract",
      "assert",
      "case",
      "class",
      "const",
      "default",
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
//      "static",
      "strictfp",
      "super",
      "switch",
      "synchronized",
      "this",
      "throws",
      "transient",
      "volatile",
      "try",
      "catch",
      "finally"
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
