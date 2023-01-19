package org.ria.example;

import org.junit.jupiter.api.Test;
import org.ria.Script;

public class PassFunctionTest {

  @Test
  public void test() {
    new Script().run(ResourceUtil.resourceAsString("passFunction.ria"));
  }

}
