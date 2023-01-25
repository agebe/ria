package org.ria;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.ria.firewall.AccessDeniedException;
import org.ria.firewall.ConstructorRule;
import org.ria.firewall.Firewall;
import org.ria.firewall.RuleAction;

public class FirewallConstructorTest {

  @Test
  public void denyAll() {
    assertThrows(AccessDeniedException.class, () -> new Script()
        .setFirewall(new Firewall()
            .setDefaultConstructorAction(RuleAction.DENY))
        .run("""
        javasrc '''
        public class A {
        }
        ''';
        new A();
        """));
  }

  @Test
  public void denyA() {
    assertThrows(AccessDeniedException.class, () -> new Script()
        .setFirewall(new Firewall()
            .setConstructorRules(List.of(new ConstructorRule(null, "A", RuleAction.DENY))))
        .run("""
        javasrc '''
        public class A {
        }
        ''';
        javasrc '''
        public class B {
        }
        ''';
        println(new B());
        println(new A());
        """));
  }

}
