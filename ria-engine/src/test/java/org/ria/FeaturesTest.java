package org.ria;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class FeaturesTest {

  @Test
  public void disabledDependenciesTest() {
    assertThrows(ScriptException.class, () -> new Script()
    .setFeatures(new Features().setDependenciesEnabled(false))
    .setQuiet(true)
    .run("""
        dependencies {
          'org.apache.commons:commons-lang3:3.12.0'
        }
        StringUtils.isBlank('123');
        """));
  }

  @Test
  public void disabledJavaSourceTest() {
    assertThrows(ScriptException.class, () -> new Script()
    .setFeatures(new Features().setJavaSourceEnabled(false))
    .setQuiet(true)
    .run("""
        javasrc '''
          public class A {}
        ''';
        new A();
        """));
  }

  @Test
  public void disabledLoopsTest() {
    assertThrows(ScriptException.class, () -> new Script()
    .setFeatures(new Features().setLoopsEnabled(false))
    .run("""
        for(var s : List.of('a','b','c')) {
          println(s);
        }
        """));
  }

}
