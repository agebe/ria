package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ImportTest {

  @Test
  public void importTest() {
    int i = new Script().evalInt("""
        import java.math.BigInteger;
        BigInteger.TEN;
        """);
    assertEquals(10, i);
  }

  @Test
  public void importAsterisk() {
    int i = new Script().evalInt("""
        import java.math.*;
        BigInteger.TEN;
        """);
    assertEquals(10, i);
  }

  @Test
  public void importStaticTest() {
    int i = new Script().evalInt("""
        import static java.math.BigInteger.TEN;
        TEN
        """);
    assertEquals(10, i);
  }

  @Test
  public void importStaticAsteriksTest() {
    int i = new Script().evalInt("""
        import static java.math.BigInteger.*;
        TEN
        """);
    assertEquals(10, i);
  }

  @Test
  public void importStaticAsteriksTest2() {
    boolean b = new Script().evalPredicate("""
        import static org.apache.commons.lang3.StringUtils.*;
        isBlank("");
        """);
    assertTrue(b);
  }

}
