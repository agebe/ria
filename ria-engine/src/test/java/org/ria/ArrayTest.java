/*
 * Copyright 2023 Andre Gebers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ria;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class ArrayTest {

  @Test
  public void simple() {
    new Script().run("arrayof []");
  }

  @Test
  public void stringArray() {
    assertArrayEquals(new String[] { "foo", "bar" }, (String[])new Script().run("arrayof ['foo', 'bar']"));
  }

  @Test
  public void intArray() {
    assertArrayEquals(new int[] { 42,43,44 }, (int[])new Script().run("arrayof [42,43,44]"));
  }

  @Test
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
    // removed char literal
    assertArrayEquals(new char[] { 'a', 'b' }, (char[])new Script().run("'ab'.toCharArray()"));
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
  public void multiDimIntArrayAccess() {
    int i = new Script().evalInt("arrayof [arrayof [1,2],arrayof [3,4]][0][1]");
    assertEquals(2,i);
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
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> new Script().run("arrayof [\"foo\",\"bar\"][2]"));
  }

  @Test
  public void arrayAccessOutOfBounds2() {
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> new Script().run("arrayof [\"foo\",\"bar\"][-1]"));
  }

  @Test
  public void notArray() {
    assertThrows(ScriptException.class, () -> new Script().run("arrayof 1[0]"));
  }

  @Test
  public void newOpArrayInit() {
    assertArrayEquals(new long[] {42, 43, 44},
        (long[])new Script().run("new long[] {42,43,44}"));
  }

  @Test
  public void newOpEmptyArrayInit() {
    assertArrayEquals(new float[0], (float[])new Script().run("new float[] {}"));
  }

  @Test
  public void newOpArray() {
    assertArrayEquals(new long[] {0, 0, 0}, (long[])new Script().run("new long[1+2]"));
  }

  @Test
  public void newOpArray2() {
    // FIXME int to double conversion should happen automatically on Math.sqrt(...)
    assertArrayEquals(new Map[] {null, null, null}, (Map[])new Script().run("new java.util.Map[Math.sqrt(9.)]"));
  }

  @Test
  public void newOpArrayMultiLong() {
    assertArrayEquals(new long[2][], (long[][])new Script().run("new long[2][]"));
  }

  @Test
  public void newOpArrayMultiLongWrongDims() {
    assertThrows(ScriptException.class, () -> new Script().run("new long[][2]"));
  }

  @Test
  public void newOpArrayMultiFloat() {
    assertArrayEquals(new float[2][][],
        (float[][][])new Script().run("new float[2][][]"));
  }

  @Test
  public void newOpArrayMultiList() {
    assertArrayEquals(new List[4][2][][][],
        (List[][][][][])new Script().run("new java.util.List[4][2][][][]"));
  }

  @Test
  public void newOpArrayMulti2() {
    assertArrayEquals(new long[][] {null, {1,2,3}, {}},
        (long[][])new Script().run("new long[][] {null, {1,2,3}, {}}"));
  }

  @Test
  public void newOpArrayMulti3() {
    assertArrayEquals(new long[][][] { {{},{},{},{0}}, null, {}},
        (long[][][])new Script().run("new long[][][] { {{},{},{},{0}}, null, {}}"));
  }

  @Test
  public void newOpArrayMulti4() {
    assertArrayEquals(new long[][] {{1,2,3},{4,5,6}},
        (long[][])new Script().run("new long[][] {{1,2,3},{4,5,6}}"));
  }

  @Test
  public void arrayLength() {
    assertEquals(1024, new Script().run("new int[1024].length"));
  }

  @Test
  public void arrayLength2() {
    assertEquals(5, new Script().run("arrayof [1,2,3,4,5].length"));
  }

}
