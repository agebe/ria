package org.rescript;

import java.io.Writer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NewOpTest {

  public static class Concat2 {
    Object o1;
    Object o2;
    public Concat2(Object o1, Object o2) {
      this.o1 = o1;
      this.o2 = o2;
    }
    @Override
    public String toString() {
      if((o1 != null) && (o2 != null)) {
        return o1.toString() + o2.toString();
      } else if(o1 != null) {
        return o1.toString();
      } else if(o2 != null) {
        return o2.toString();
      } else {
        return null;
      }
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

}
