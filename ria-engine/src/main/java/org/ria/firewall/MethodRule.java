package org.ria.firewall;

import java.lang.reflect.Method;

public interface MethodRule extends Rule {
  boolean matches(Method method);
}
