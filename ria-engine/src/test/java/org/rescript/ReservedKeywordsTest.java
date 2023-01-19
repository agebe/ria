package org.rescript;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class ReservedKeywordsTest {

  @ParameterizedTest
  @MethodSource("org.rescript.parser.ReservedKeywords#reservedKeywords")
  public void reservedKeywordsTest(String keyword) {
    assertThrows(Exception.class, () -> new Script().run("var %s = 1;".formatted(keyword)));
  }
}
