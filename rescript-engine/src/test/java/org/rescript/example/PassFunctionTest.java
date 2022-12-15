package org.rescript.example;

import org.junit.jupiter.api.Test;
import org.rescript.Script;

public class PassFunctionTest {

  @Test
  public void test() {
    new Script().run(ResourceUtil.resourceAsString("PassFunction.bs"));
  }

}
