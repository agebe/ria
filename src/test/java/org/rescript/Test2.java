package org.rescript;

import org.junit.jupiter.api.Test;
import org.rescript.Test1.TestInner1;
import static org.rescript.Test1.*;
import static org.rescript.Test1.TestInner1.*;

public class Test2 {

  @Test
  public void test() {
    String TestInner1 = "foo";
    TestInner1 t1 =  new TestInner1();
    TestInner2 t2;
    TestInner3 t3;
    System.out.println(TestInner1.class);
  }

}
