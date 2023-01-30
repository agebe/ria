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

import java.io.Writer;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NewOpTest {

  public static class Concat2 {
    private ConcatAll all;
    public Concat2(Object o1, Object o2) {
      all = new ConcatAll(o1, o2);
    }
    @Override
    public String toString() {
      return all.toString();
    }
  }

  public static class ConcatAll {
    private Object[] o;
    public ConcatAll(Object... objects) {
      this.o = objects;
    }
    @Override
    public String toString() {
      return Arrays.stream(o)
          .filter(Objects::nonNull)
          .map(Object::toString)
          .collect(Collectors.joining());
    }
  }

  public static class ConcatAllPrefixRepeat {
    private String prefix;
    private int repeat;
    private String[] o;
    public ConcatAllPrefixRepeat(String prefix, int repeat, String... objects) {
      this.prefix = prefix;
      this.repeat = repeat;
      this.o = objects;
    }
    @Override
    public String toString() {
      String s = Arrays.stream(o)
          .filter(Objects::nonNull)
          .collect(Collectors.joining());
      return prefix + StringUtils.repeat(s, repeat);
    }
  }

  public static class Base {
    public void foo() {
    }
    public static void base() {
      System.out.println("base");
    }
    public static void base2() {
      System.out.println("base2");
    }
  }

  public static class A extends Base {
    public void foo() {
    }
    public static void base() {
      System.out.println("a");
    }
  }

  @Test
  public void new1() {
    new Script().run("new java.lang.Object()");
  }

  @Test
  public void new2() {
    new Script().run("new Object()");
  }

  @Test
  public void new3() {
    String s = new Script().runReturning("""
        new StringBuilder().append("NewOpTest").toString()
        """, String.class);
    Assertions.assertEquals("NewOpTest", s);
  }

  @Test
  public void new4() throws Exception {
    Writer w = new Script()
        .runReturning("""
        new BufferedWriter(new StringWriter())
        """, Writer.class);
    Assertions.assertNotNull(w);
    w.write("foo");
    w.close();
  }

  @Test
  public void concat() throws Exception {
    String s = new Script()
        .addImport(this.getClass().getPackageName()+".*")
        .runReturning("""
            new NewOpTest.Concat2("12", "34").toString();
            """, String.class);
    Assertions.assertEquals("1234", s);
  }

  @Test
  public void concatAll() throws Exception {
    String s = new Script()
        .addImport(this.getClass().getPackageName()+".*")
        .runReturning("""
            new NewOpTest.ConcatAll("12", "34", "56").toString();
            """, String.class);
    Assertions.assertEquals("123456", s);
  }

  @Test
  public void concatRepeat() throws Exception {
    String s = new Script()
        .addImport(this.getClass().getPackageName()+".*")
        .runReturning("""
            new NewOpTest.ConcatAllPrefixRepeat("foo:", 3, "12", "34", "56").toString();
            """, String.class);
    Assertions.assertEquals("foo:123456123456123456", s);
  }

  @Test
  public void concatRepeat2() throws Exception {
    String s = new Script()
        .addImport(this.getClass().getPackageName()+".*")
        .runReturning("""
            new NewOpTest.ConcatAllPrefixRepeat("foo:", 3).toString();
            """, String.class);
    Assertions.assertEquals("foo:", s);
  }

}
