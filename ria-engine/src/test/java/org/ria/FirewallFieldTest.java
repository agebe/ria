package org.ria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.ria.firewall.AccessDeniedException;
import org.ria.firewall.FieldAccess;
import org.ria.firewall.Firewall;
import org.ria.firewall.RuleAction;

public class FirewallFieldTest {

  @Test
  public void denyAll() {
    assertThrows(AccessDeniedException.class, () -> new Script().
        setFirewall(new Firewall()
            .setDefaultFieldAction(RuleAction.DENY))
        .run("""
        javasrc '''
        public class A {
          public int a = 3;
        }
        ''';
        var a = new A();
        println(a.a);
        """));
  }

  @Test
  public void dropAll() {
    assertNull(new Script().
        setFirewall(new Firewall()
            .setDefaultFieldAction(RuleAction.DROP))
        .run("""
        javasrc '''
        public class A {
          public Integer a = 3;
        }
        ''';
        var a = new A();
        a.a;
        """));
  }

  @Test
  public void dropSet() {
    assertEquals(3, new Script().
        setFirewall(new Firewall()
            .addFieldRule(Set.of(FieldAccess.SET), null, null, null, RuleAction.DROP))
        .run("""
        javasrc '''
        public class A {
          public int a = 3;
        }
        ''';
        var a = new A();
        a.a = 5;
        a.a;
        """));
  }

}
