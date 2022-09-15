package org.rescript;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OpTests {

  @Test
  public void parens() {
    Assertions.assertEquals(1, new Script().evalInt("((1));"));
  }

}
