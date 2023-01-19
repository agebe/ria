package org.ria;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class MapLiteralTest {

  @Test
  public void emptyMap() {
    assertEquals(Collections.emptyMap(), new Script().run("[:]"));
  }

  @Test
  public void singletonMap() {
    assertEquals(Map.of(1,2), new Script().run("[1:2]"));
  }

  @Test
  public void map() {
    assertEquals(Map.of(1,2, "foo", "bar"), new Script().run("[1:2, \"foo\":\"bar\"]"));
  }

  @Test
  public void map2() {
    assertEquals(Map.of(1,2,3,4,5,6,7,8), new Script().run("[1:2, 3:4, 5:6, 7:8]"));
  }

}
