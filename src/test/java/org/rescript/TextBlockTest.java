package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TextBlockTest {

  @Test
  public void singleQuoteTextblock() {
    String s = (String)new Script().run("""
        var s = '''
        a
        b
        ''';
        println(s);
        s;
        """);
    assertEquals("""
        a
        b
        """, s);
  }

  @Test
  public void doubleQuoteTextblock() {
    String script = """
        var s = \"\"\"
       a
        b\"\"\";
        println(s);
        s;
        """;
    System.out.println(script);
    String s = (String)new Script().run(script);
    System.out.println(s);
    String expect = """
      a
       b""";
    System.out.println(expect);
    assertEquals(expect, s);
  }

}
