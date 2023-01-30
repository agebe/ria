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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.ria.firewall.AccessDeniedException;
import org.ria.firewall.FieldAccess;
import org.ria.firewall.DefaultFirewall;
import org.ria.firewall.RuleAction;

public class FirewallFieldTest {

  @Test
  public void denyAll() {
    assertThrows(AccessDeniedException.class, () -> new Script().
        setFirewall(new DefaultFirewall()
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
        setFirewall(new DefaultFirewall()
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
        setFirewall(new DefaultFirewall()
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
