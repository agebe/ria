/*
 * Copyright 2023 Andre Gebers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ria;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.ria.dependency.Dependencies;
import org.ria.dependency.GradleShortDependency;
import org.ria.firewall.AccessDeniedException;
import org.ria.firewall.DefaultFirewall;
import org.ria.firewall.SimpleMethodRule;
import org.ria.firewall.RuleAction;

public class FirewallMethodTest {

  @Test
  public void simple() {
    assertThrows(AccessDeniedException.class, () -> new Script()
        .setFirewall(new DefaultFirewall()
            .setMethodRules(List.of(new SimpleMethodRule("java.lang", "System", null, RuleAction.DENY))))
        .run("""
        System.nanoTime();
        """));
  }

  @Test
  public void stringUtils() {
    new Script()
    .setFirewall(new DefaultFirewall()
        .setDefaultMethodAction(RuleAction.DENY)
        .setDefaultFieldAction(RuleAction.DENY)
        .setDefaultConstructorAction(RuleAction.DENY)
        .addMethodRule("org.apache.commons.lang3", "StringUtils", null, RuleAction.ACCEPT)
     // gradle uses: void org.gradle.internal.io.LinePerThreadBufferingOutputStream.println(boolean)
        .addMethodRule(null, null, "println", RuleAction.ACCEPT))
    .setVariable("dependencies", new Dependencies(
        List.of(new GradleShortDependency("org.apache.commons:commons-lang3:3.12.0"))))
    .run("""
        println(StringUtils.isBlank('123'));
        """);
  }

  @Test
  public void drop() {
    Object o = new Script()
    .setFirewall(new DefaultFirewall()
        .addMethodRule("java.io", "PrintStream", "println", RuleAction.DROP)
        .addMethodRule("java.lang", "String", "concat", RuleAction.DROP))
    .run("""
        println('12345');
        '1'.concat('2');
        """);
    assertNull(o);
  }

}
