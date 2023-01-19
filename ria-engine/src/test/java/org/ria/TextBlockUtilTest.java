package org.ria;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.ria.parser.TextBlockUtil;

public class TextBlockUtilTest {

  @Test
  public void tb1() {
    String tb1 = "'''\n11XX12\u202F \t\n\t\t\t'''";
    String s = TextBlockUtil.toString(tb1);
    assertEquals("11XX12\n", s);
  }

}
