package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.rescript.symbol.java.RUtils;

public class RUtilsTest {

  public static class StaticInner {
    
  }

  public class Inner {
    
  }

  @Test
  public void findClass() {
    Class<?> cls = RUtils.findClass("java.lang.Object");
    assertEquals(Object.class, cls);
  }

  @Test
  public void findClass2() {
    Class<?> cls = RUtils.findClass(this.getClass().getPackageName(), "RUtilsTest.StaticInner");
    assertEquals(StaticInner.class, cls);
  }

  @Test
  public void findClass3() {
    Class<?> cls = RUtils.findClass(this.getClass().getPackageName(), "RUtilsTest.Inner");
    assertEquals(Inner.class, cls);
  }


}
