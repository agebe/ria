package org.ria.firewall;

import java.lang.reflect.Constructor;

public interface ConstructorRule extends Rule {
  boolean matches(Constructor<?> c);
}
