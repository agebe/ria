package org.ria;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.ria.dependency.Dependencies;
import org.ria.dependency.GradleShortDependency;
import org.ria.firewall.AccessDeniedException;
import org.ria.firewall.Firewall;
import org.ria.firewall.MethodRule;
import org.ria.firewall.RuleAction;

public class FirewallMethodTest {

  @Test
  public void simple() {
    assertThrows(AccessDeniedException.class, () -> new Script()
        .setFirewall(new Firewall()
            .setMethodRules(List.of(new MethodRule("java.lang", "System", null, RuleAction.DENY))))
        .run("""
        System.nanoTime();
        """));
  }

  @Test
  public void stringUtils() {
    new Script()
    .setFirewall(new Firewall()
        .setDefaultMethodAction(RuleAction.DENY)
        .setDefaultFieldAction(RuleAction.DENY)
        .setDefaultConstructorAction(RuleAction.DENY)
        .setMethodRules(List.of(
            new MethodRule("org.apache.commons.lang3", "StringUtils", null, RuleAction.ACCEPT),
         // gradle uses: void org.gradle.internal.io.LinePerThreadBufferingOutputStream.println(boolean)
            new MethodRule(null, null, "println", RuleAction.ACCEPT)))
        )
    .setVariable("dependencies", new Dependencies(
        List.of(new GradleShortDependency("org.apache.commons:commons-lang3:3.12.0"))))
    .run("""
        println(StringUtils.isBlank('123'));
        """);
  }

  @Test
  public void drop() {
    Object o = new Script()
    .setFirewall(new Firewall()
        .setMethodRules(List.of(
            new MethodRule("java.io", "PrintStream", "println", RuleAction.DROP),
            new MethodRule("java.lang", "String", "concat", RuleAction.DROP)))
        )
    .run("""
        println('12345');
        '1'.concat('2');
        """);
    assertNull(o);
  }

}
