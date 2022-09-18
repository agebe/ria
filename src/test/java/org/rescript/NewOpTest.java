package org.rescript;

import java.io.Writer;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

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
    Writer w = new ScriptBuilder()
        .addImport("java.io.*")
        .create()
        .runReturning("""
        new BufferedWriter(new StringWriter())
        """, Writer.class);
    Assertions.assertNotNull(w);
    w.write("foo");
    w.close();
  }

  @Test
  public void concat() throws Exception {
    String s = new ScriptBuilder()
    .addImport(this.getClass().getPackageName()+".*")
    .create()
    .runReturning("""
    new NewOpTest.Concat2("12", "34").toString();
    """, String.class);
    Assertions.assertEquals("1234", s);
  }

  @Test
  public void concatAll() throws Exception {
    String s = new ScriptBuilder()
    .addImport(this.getClass().getPackageName()+".*")
    .create()
    .runReturning("""
    new NewOpTest.ConcatAll("12", "34", "56").toString();
    """, String.class);
    Assertions.assertEquals("123456", s);
  }

}
