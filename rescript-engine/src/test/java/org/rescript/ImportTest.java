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

  @Test
  public void importFunctionPackage() {
    // function is a keyword in the script language and can not be used in package names.
    // to make this work also allow the import from a string
    int i = new Script().evalInt("""
        import 'org.rescript.function.A';
        var a = A.test();
        """);
    assertEquals(42, i);
  }

}
