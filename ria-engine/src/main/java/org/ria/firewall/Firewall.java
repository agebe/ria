package org.ria.firewall;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.ria.value.Value;

public interface Firewall {

  void checkAndSet(Field f, Object target, Value v);

  Object checkAndGet(Field f, Object target);

  Object checkAndInvoke(Constructor<?> c, Object[] parameters);

  Object checkAndInvoke(Method m, Object target, Object[] parameters);

}
