package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public class WhileLoop {

  @Test
  public void iteratorLoop() {
    int i = new ScriptBuilder().addImport("java.util.*").create().evalInt("""
        // FIXME this gives an IllegalAccessException:
        // Caused by: java.lang.IllegalAccessException: class org.rescript.run.FunctionCaller cannot access a member of class java.util.ImmutableCollections$AbstractImmutableList (in module java.base) with modifiers "public"
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

}
