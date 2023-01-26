package org.ria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public class WhileLoop {

  @Test
  public void iteratorLoop() {
    int i = new Script().evalInt("""
        var iter = List.of(1,2,3).iterator();
        while(iter.hasNext()) {
          iter.next();
        }
        """);
    assertEquals(3, i);
  }

  @Test
  public void whileFalse() {
    assertEquals(1, new Script().evalInt("1;while(false) 2;"));
  }

  @Test
  public void while2() {
    assertFalse(new Script().evalPredicate("var a = true;while(a) a = false;"));
  }

  @Test
  public void whileBreak() {
    assertEquals(2, new Script().evalInt("1;while(true) {2;break;3;}"));
  }

  @Test
  public void whileContinue() {
    assertEquals(42, new Script().evalInt("1;var i = 0;var a = 42;while(i < 3) {i++;println(i);continue;a=0;}a;"));
  }

}
