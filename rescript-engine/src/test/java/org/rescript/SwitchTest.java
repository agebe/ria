package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class SwitchTest {

  @Test
  public void simple() {
    assertNull(new Script().run("switch(null) {}"));
  }

  @Test
  public void simpleColonCase() {
    assertEquals(1, new Script().run("switch(null) {case null: 1;}"));
  }

  @Test
  public void colonCase2() {
    assertEquals("b", new Script().run("switch(2) {case 1: 'a';case 2: 'b';}"));
  }

  @Test
  public void colonCase3() {
    assertEquals("b", new Script().run("switch(1) {case 1: 'a';case 2: 'b';}"));
  }

  @Test
  public void colonCase4() {
    assertEquals("a", new Script().run("switch(1) {case 1: 'a';break;case 2: 'b';}"));
  }

  @Test
  public void defaultColonCase() {
    assertEquals("b", new Script().run("switch(3) {case 1: 'a';default: 'b';}"));
  }

  @Test
  public void defaultColonCase2() {
    assertEquals("a", new Script().run("switch(3) {default: 'b';case 1: 'a';}"));
  }

  @Test
  public void defaultColonCase3() {
    assertEquals("b", new Script().run("switch(3) {default: 'b';break;case 1: 'a';}"));
  }

  @Test
  public void defaultColonCase4() {
    assertThrows(RuntimeException.class,
        () -> new Script().run("switch(3) {default: throw new RuntimeException('test');}"));
  }

  @Test
  public void enumColonCase() {
    int i = new Script().evalInt("""
        var day = DayOfWeek.TUESDAY;
        switch(day) {
          case DayOfWeek.MONDAY: 1;break;
          case DayOfWeek.TUESDAY: 2;break;
          case DayOfWeek.WEDNESDAY: 3;break;
          default: throw new RuntimeException('test');
        }
        """);
    assertEquals(2, i);
  }

  @Test
  public void enumColonCase2() {
    int i = new Script().evalInt("""
        var day = DayOfWeek.TUESDAY;
        var a = switch(day) {
          case DayOfWeek.MONDAY: 1;break;
          case DayOfWeek.TUESDAY: 2;break;
          case DayOfWeek.WEDNESDAY: 3;break;
          default: throw new RuntimeException('test');
        };
        a;
        """);
    assertEquals(2, i);
  }

}
