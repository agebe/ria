package org.ria.firewall;

import java.lang.reflect.Field;

public interface FieldRule extends Rule {
  boolean matches(Field field, FieldAccess fieldAccess);
}
