package org.rescript.symbol;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RUtils {

  private static final Logger log = LoggerFactory.getLogger(RUtils.class);

  public static Field findStaticField(Class<?> cls, String name) {
    try {
      for(Field field : cls.getDeclaredFields()) {
        if(java.lang.reflect.Modifier.isStatic(field.getModifiers()) && field.getName().equals(name)) {
          return field;
        }
      }
      return null;
    } catch(Exception e) {
      log.trace("findStaticField failed class '{}', field '{}'", cls, name, e);
      return null;
    }
  }

  public static Field findField(Class<?> cls, String name) {
    try {
      return cls.getField(name);
    } catch(Exception e) {
      return null;
    }
  }

  public static Class<?> innerClass(Class<?> cls, String name) {
    Class<?>[] classes = cls.getDeclaredClasses();
    return Arrays.stream(classes)
        .filter(c -> c.getSimpleName().equals(name))
        .findFirst()
        .orElse(null);
  }

}
