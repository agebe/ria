package org.rescript.symbol;

import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

  public static <T extends Executable> T matchSignature(Class<?>[] params, List<T> executables) {
    return matchSignatureExactly(params, executables)
        .orElseGet(() -> matchSignatureFuzzy(params, executables).orElse(null));
  }

  public static <T extends Executable> Optional<T> matchSignatureExactly(Class<?>[] params, List<T> executables) {
    return executables.stream()
        .filter(e -> matchSignatureExactly(params, e))
        .findFirst();
  }

  public static boolean matchSignatureExactly(Class<?>[] params, Executable executable) {
    if(executable.getParameterTypes().length != params.length) {
      return false;
    }
    for(int i=0;i<params.length;i++) {
      Class<?> ep = executable.getParameterTypes()[i];
      Class<?> pt = params[i];
      if(!Objects.equals(ep, pt)) {
        log.debug("match parameters exactly failed '{}', method parameter type '{}', supplied types '{}'",
            executable.getName(), Arrays.toString(executable.getParameterTypes()), Arrays.toString(params));
        return false;
      }
    }
    log.debug("match parameters exactly success '{}', method parameter type '{}', supplied types '{}'",
        executable.getName(), Arrays.toString(executable.getParameterTypes()), Arrays.toString(params));
    return true;
  }

  public static <T extends Executable> Optional<T> matchSignatureFuzzy(Class<?>[] params,
      List<T> executables) {
    return executables.stream()
        .filter(e -> matchSignatureFuzzy(params, e))
        .findFirst();
  }

  public static boolean matchSignatureFuzzy(Class<?>[] params, Executable executable) {
    if(executable.isVarArgs()) {
      log.warn("can't match varargs method '{}' currently (not implemented)", executable.getName());
      return false;
    }
    Class<?>[] methodParams = executable.getParameterTypes();
    if(methodParams.length != params.length) {
      return false;
    }
    for(int i=0;i<params.length;i++) {
      Class<?> mp = methodParams[i];
      Class<?> pt = params[i];
      if(!mp.isAssignableFrom(pt)) {
        log.debug("match parameters fuzzy failed '{}', method parameter type '{}', supplied types '{}'",
            executable.getName(), Arrays.toString(executable.getParameterTypes()), Arrays.toString(params));
        return false;
      }
    }
    log.debug("match parameters fuzzy success '{}', method parameter type '{}', supplied types '{}'",
        executable.getName(), Arrays.toString(executable.getParameterTypes()), Arrays.toString(params));
    return true;
  }

}
