package org.rescript;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ArrayTest {

  @Test
  public void simple() {
    new Script().run("arrayof []");
  }

  @Test
  public void stringArray() {
    assertArrayEquals(new String[] { "foo", "bar" }, (String[])new Script().run("""
        arrayof ["foo", "bar"]
        """));
  }

  @Test
  public void intArray() {
    assertArrayEquals(new int[] { 42,43,44 }, (int[])new Script().run("arrayof [42,43,44]"));
  }

  public void intArrayEq() {
    // this calls Arrays.equals(...) behind the scenes
    assertTrue(new Script().evalPredicate("arrayof [1,2] == arrayof [1,2]"));
  }

  @Test
  public void mixedArray() {
    assertArrayEquals(new Object[] { "foo", 42 }, (Object[])new Script().run("arrayof [\"foo\", 42]"));
  }

  @Test
  public void mixedIntLongArray() {
    assertArrayEquals(new long[] { 42, Long.MAX_VALUE }, (long[])new Script().run("arrayof [42, Long.MAX_VALUE]"));
  }

  @Test
  public void floatArray() {
    assertArrayEquals(new float[] { .5f, 1.5f }, (float[])new Script().run("arrayof [.5f, 1.5f]"));
  }

  @Test
  public void doubleArray() {
    assertArrayEquals(new double[] { .5, 1.5 }, (double[])new Script().run("arrayof [.5, 1.5]"));
  }

  @Test
  public void booleanArray() {
    assertArrayEquals(new boolean[] { true, false }, (boolean[])new Script().run("arrayof [true, false]"));
  }

  @Test
  public void charArray() {
    assertArrayEquals(new char[] { 'a', 'b' }, (char[])new Script().run("arrayof ['a','b']"));
  }

  @Test
  public void multiDimArray() {
    Object[] a = (Object[])new Script().run("arrayof [arrayof [1,2],3]");
    assertArrayEquals(new int[] {1,2}, (int[])a[0]);
    assertEquals(3, a[1]);
  }

  @Test
  public void multiDimIntArray() {
    int[][] a = (int[][])new Script().run("arrayof [arrayof [1,2],arrayof [3,4]]");
    assertArrayEquals(new int[] {1,2}, a[0]);
    assertArrayEquals(new int[] {3,4}, a[1]);
  }

  @Test
  public void javaFunctionCall() {
    new Script().run("org.junit.jupiter.api.Assertions.assertArrayEquals(arrayof [1,2,3],arrayof [1,2,3])");
  }

  @Test
  public void scriptFunctionCall() {
    assertEquals("[I", new Script().run("function f1(array){typeof array;} f1(arrayof [1,2,3]);"));
  }

  @Test
  public void arrayAccess() {
    assertEquals(43, new Script().run("arrayof [42,43,44][1]"));
  }

  @Test
  public void arrayAccess2() {
    assertEquals("foo", new Script().run("arrayof [\"foo\",\"bar\"][0]"));
  }

  @Test
  public void arrayAccessOutOfBounds() {
    assertThrows(ScriptException.class, () -> new Script().run("arrayof [\"foo\",\"bar\"][2]"));
  }

  @Test
  public void arrayAccessOutOfBounds2() {
    assertThrows(ScriptException.class, () -> new Script().run("arrayof [\"foo\",\"bar\"][-1]"));
  }

  @Test
  public void notArray() {
    assertThrows(ScriptException.class, () -> new Script().run("arrayof 1[0]"));
  }

}
