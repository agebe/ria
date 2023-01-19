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

  @Test
  public void empty() {
    String s = (String)new Script().run("""
        var s = '''
        ''';
        """);
    assertEquals("", s);
  }

  @Test
  public void tb1() {
    String s = (String)new Script().run("""
            var tb1 = '''
          XX12''';
            tb1;
        """);
    assertEquals("XX12", s);
  }

  @Test
  public void twoBlocks() {
    new Script().run("""
        var block1 = '''
        block1''';
        var block2 = '''
        block2''';
        println(block1);
        println(block2);
        """);
  }

}
