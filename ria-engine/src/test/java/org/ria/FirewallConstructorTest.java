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

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.ria.firewall.AccessDeniedException;
import org.ria.firewall.DefaultFirewall;
import org.ria.firewall.RuleAction;

public class FirewallConstructorTest {

  @Test
  public void denyAll() {
    assertThrows(AccessDeniedException.class, () -> new Script()
        .setFirewall(new DefaultFirewall()
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
        .setFirewall(new DefaultFirewall()
            .addConstructorRule(null, "A", RuleAction.DENY))
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
