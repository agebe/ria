package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class ForEachTest {

  @Test
  public void simple() {
    new Script().run("for(i : new java.util.ArrayList()) println(i);");
  }

  @Test
  public void iterate() {
    new Script().run("for(i : java.util.List.of(1,2,3)) println(typeof i);");
  }

  @Test
  public void iterateWithType() {
    new Script().run("for(int i : java.util.List.of(1,2,3)) println(typeof i);");
  }

  @Test
  public void iterateWithWrongType() {
    assertThrows(ScriptException.class,
        () -> new Script().run("for(boolean i : java.util.List.of(1,2,3)) println(i);"));
  }

  @Test
  public void wrongIterable() {
    assertThrows(ScriptException.class, () -> new Script().run("for(i : \"1 2 3\") println(i);"));
  }

  @Test
  public void breakContinue() {
    Script s = new Script();
    s.setVariable("l", new ArrayList<Integer>());
    s.run("""
        for(var i : java.util.List.of(1,2,3,4,5,6,7,8,9)) {
          if(i == 2) {
            continue;
          } else if(i > 4) {
            break;
          } else {
            l.add(i);
          }
        }
        """);
    assertEquals(List.of(1,3,4), s.getVariable("l"));
  }

  @Test
  public void breakContinueOverListLiteral() {
    Script s = new Script();
    s.setVariable("l", new ArrayList<Integer>());
    s.run("""
        for(var i : [1,2,3,4,5,6,7,8,9]) {
          if(i == 2) {
            continue;
          } else if(i > 4) {
            break;
          } else {
            l.add(i);
          }
        }
        """);
    assertEquals(List.of(1,3,4), s.getVariable("l"));
  }

  @Test
  public void breakContinueOverArray() {
    Script s = new Script();
    s.setVariable("l", new ArrayList<Integer>());
    s.run("""
        for(var i : arrayof [1,2,3,4,5,6,7,8,9]) {
          if(i == 2) {
            continue;
          } else if(i > 4) {
            break;
          } else {
            l.add(i);
          }
        }
        """);
    assertEquals(List.of(1,3,4), s.getVariable("l"));
  }

}
