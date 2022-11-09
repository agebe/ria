package org.rescript.symbol.java;

import java.lang.reflect.Array;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.rescript.ScriptException;
import org.rescript.run.MethodReferenceInvocationHandler;
import org.rescript.run.ScriptContext;
import org.rescript.run.ScriptLambdaInvocationHandler;
import org.rescript.value.Value;
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

  public static LinkedList<String> splitTypeName(String name) {
    return new LinkedList<>(List.of(StringUtils.split(name, ".")));
  }

  public static Class<?> findClass(String prefix, String name) {
    return findClass(prefix + "." + name);
  }

  /**
   * finds the class of the given name. The name can also be an inner class
   * @param name - name of the class separated by dots
   * @return
   */
  public static Class<?> findClass(String name) {
    Class<?> cls = forName(name);
    if(cls != null) {
      return cls;
    }
    LinkedList<String> split = splitTypeName(name);
    String outer = "";
    for(;;) {
      if(split.isEmpty()) {
        break;
      }
      if(cls == null) {
        // find outer class
        outer = StringUtils.isBlank(outer)?split.removeFirst():outer + "." +split.removeFirst();
        cls = forName(outer);
      } else {
        cls = innerClass(cls, split.removeFirst());
        if(cls == null) {
          return null;
        }
      }
    }
    return cls;
  }

  public static Class<?> forName(String name) {
    try {
      return Class.forName(name);
    } catch(Exception e) {
      return null;
    }
  }

  public static <T extends Executable> T matchSignature(Value[] params, List<T> executables) {
    return matchSignatureExactly(params, executables)
        .orElseGet(() -> matchSignatureFuzzy(params, executables).orElse(null));
  }

  public static <T extends Executable> Optional<T> matchSignatureExactly(Value[] params, List<T> executables) {
    return executables.stream()
        .filter(e -> matchSignatureExactly(params, e))
        .findFirst();
  }

  private static boolean isFunctionalInterface(Class<?> cls) {
    if(!cls.isInterface()) {
      return false;
    }
    Method methods[] = cls.getMethods();
    return Arrays.stream(methods)
        .filter(m -> !(m.isDefault() || Modifier.isStatic(m.getModifiers())))
        .count() == 1;
  }

  private static boolean matchLambda(Class<?> expected, Value supplied) {
    // TODO check that number of functional interface parameters match supplied function parameter size
    return (supplied.isFunction() || supplied.isMethod()) && isFunctionalInterface(expected);
  }

  public static boolean matchSignatureExactly(Value[] params, Executable executable) {
    if(executable.getParameterTypes().length != params.length) {
      return false;
    }
    for(int i=0;i<params.length;i++) {
      Class<?> ep = executable.getParameterTypes()[i];
      if(matchLambda(ep, params[i])) {
        continue;
      }
      Class<?> pt = params[i].type();
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

  public static <T extends Executable> Optional<T> matchSignatureFuzzy(Value[] params,
      List<T> executables) {
    return executables.stream()
        .filter(e -> e.isVarArgs()?matchSignatureVarargs(params, e):matchSignatureFuzzy(params, e))
        .findFirst();
  }

  public static boolean matchSignatureVarargs(Value[] params, Executable executable) {
    if(!executable.isVarArgs()) {
      return matchSignatureFuzzy(params, executable);
    }
    Class<?>[] methodParams = executable.getParameterTypes();
    // check params before the vararg first
    for(int i=0;i<methodParams.length-1;i++) {
      if(params.length < (i+1)) {
        return false;
      }
      Class<?> mp = methodParams[i];
      if(matchLambda(mp, params[i])) {
        continue;
      }
      Class<?> pt = params[i].type();
      if(!mp.isAssignableFrom(pt)) {
        return false;
      }
    }
    Class<?> vararg = methodParams[methodParams.length-1];
    if(!vararg.isArray()) {
      throw new ScriptException("expected vararg method param to be an array type but got " + vararg);
    }
    Class<?> varargType = vararg.getComponentType();
    log.debug("vararg type '{}'", varargType);
    // run through the remaining parameters which are the varargs.
    // if there are none this is ok too
    for(int i=methodParams.length;i<params.length;i++) {
      Class<?> pt = params[i].type();
      if(!varargType.isAssignableFrom(pt)) {
        return false;
      }
    }
    log.debug("match parameters varargs success '{}', method parameter type '{}', supplied types '{}'",
        executable.getName(), Arrays.toString(executable.getParameterTypes()), Arrays.toString(params));
    return true;
  }

  public static boolean matchSignatureFuzzy(Value[] params, Executable executable) {
    if(executable.isVarArgs()) {
      return matchSignatureVarargs(params, executable);
    }
    Class<?>[] methodParams = executable.getParameterTypes();
    if(methodParams.length != params.length) {
      return false;
    }
    for(int i=0;i<params.length;i++) {
      Class<?> mp = methodParams[i];
      if(matchLambda(mp, params[i])) {
        continue;
      }
      Class<?> pt = params[i].type();
      if(!isParameterTypeMatch(mp, pt)) {
        log.debug("match parameters fuzzy failed '{}', method parameter type '{}', supplied types '{}'",
            executable.getName(), Arrays.toString(executable.getParameterTypes()), Arrays.toString(params));
        return false;
      }
    }
    log.debug("match parameters fuzzy success '{}', method parameter type '{}', supplied types '{}'",
        executable.getName(), Arrays.toString(executable.getParameterTypes()), Arrays.toString(params));
    return true;
  }

  public static boolean isParameterTypeMatch(Class<?> target, Class<?> from) {
    if(target.equals(Object.class)) {
      return true;
    }
    if(target.isAssignableFrom(from)) {
      return true;
    }
    // added this to get the LambdaTest.asJavaLambda() going
    // requires better fix!
    if(target.equals(int.class) && from.equals(Integer.class)) {
      return true;
    }
    // FIXME all of the following are false but should be ok for function calling
//    System.out.println(Object.class.isAssignableFrom(int.class));
//    System.out.println(Integer.class.isAssignableFrom(int.class));
//    System.out.println(int.class.isAssignableFrom(Integer.class));
//    System.out.println(int.class.isAssignableFrom(long.class));
//    System.out.println(long.class.isAssignableFrom(int.class));
//    System.out.println(Integer.class.isAssignableFrom(Long.class));
//    System.out.println(Long.class.isAssignableFrom(Integer.class));
//    https://stackoverflow.com/questions/12559634/java-autoboxing-rules
//    https://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html
    return false;
  }

  public static Object[] prepareParamsForInvoke(Executable executable, Value[] params, ScriptContext ctx) {
    if(executable.isVarArgs()) {
      Class<?>[] types = executable.getParameterTypes();
      Class<?> va = types[types.length-1];
      Class<?> ct = va.getComponentType();
      Object array = Array.newInstance(ct, params.length - (types.length - 1));
      for(int pi=0,i=(types.length-1);i<params.length;i++,pi++) {
        Array.set(array, pi, params[i].val());
      }
      Object[] newParams = new Object[types.length];
      for(int i=0;i<types.length-1;i++) {
        newParams[i] = params[i].val();
      }
      newParams[newParams.length-1] = array;
      return newParams;
    } else {
      Class<?>[] types = executable.getParameterTypes();
      Object[] preparedParams = new Object[params.length];
      for(int i=0;i<types.length;i++) {
        Class<?> expected = types[i];
        Value supplied = params[i];
        if(isFunctionalInterface(expected) && supplied.isFunction()) {
          preparedParams[i] = Proxy.newProxyInstance(
              RUtils.class.getClassLoader(),
              new Class[] {expected},
              new ScriptLambdaInvocationHandler(supplied.toFunctionValue(), ctx));
        } else if(isFunctionalInterface(expected) && supplied.isMethod()) {
          preparedParams[i] = Proxy.newProxyInstance(
              RUtils.class.getClassLoader(),
              new Class[] {expected},
              new MethodReferenceInvocationHandler(supplied.toMethodValue(), ctx));
        } else {
          preparedParams[i] = params[i].val();
        }
      }
      return preparedParams;
    }
  }

  public static List<Method> findAccessibleMethods(Class<?> cls, Object target, String name) {
    // TODO we don't have to scan the entire class/interface tree as we only require 1 accessible
    // method that matches the signature
    // for now we walk the entire tree and select the accessible methods with a matching name
    // method signature checks comes after...
    log.debug("enter findAccessibleMethods, class '{}', name '{}', '{}'", cls.getName(), name);
    List<Method> l = findAccessibleMethods(cls, target, name, new ArrayList<>());
    log.debug("found methods on class '{}', name '{}', '{}'", cls.getName(), name, l);
    return l;
  }

  private static List<Method> findAccessibleMethods(Class<?> cls, Object target, String name, List<Method> acc) {
    log.debug("enter findAccessibleMethods, class '{}', name '{}', '{}'", cls.getName(), name, acc);
    for(Method m : cls.getDeclaredMethods()) {
      if(!m.getName().equals(name)) {
        continue;
      }
      // it does not matter if the method is abstract as we are calling it on the target object
      // it only has to be accessible
      // also there are no static abstract methods in java AFAIK
      // TODO not sure about static methods though
//      if(Modifier.isAbstract(m.getModifiers())) {
//        log.debug("can't access '{}' (abstract), class '{}', name '{}', '{}', continue", name, cls.getName(), name);
//        continue;
//      }
      if(Modifier.isStatic(m.getModifiers())) {
        // static methods can be accessed through an object reference in java but the canAccess method does not like it
        if(!m.canAccess(null)) {
          log.debug("can't access '{}' (static), class '{}', name '{}', '{}', continue", name, cls.getName(), name);
          continue;
        }
      } else {
        if(target == null) {
          log.debug("can't access '{}' (instance method, null target), class '{}', name '{}', '{}', continue",
              name, cls.getName(), name);
          continue;
        }
        if(!m.canAccess(target)) {
          log.debug("can't access '{}', class '{}', name '{}', '{}', continue", name, cls.getName(), name);
          continue;
        }
      }
      log.debug("found method '{}'", m);
      acc.add(m);
    }
    log.debug("super class of '{}' is '{}'", cls, cls.getSuperclass());
    if(cls.getSuperclass() != null) {
      findAccessibleMethods(cls.getSuperclass(), target, name, acc);
    }
    // I think we also have to scan all interfaces in case of default implementations that are not on any class?!
    log.debug("interfaces of class '{}' are '{}'", cls, Arrays.toString(cls.getInterfaces()));
    for(Class<?> interf : cls.getInterfaces()) {
      findAccessibleMethods(interf, target, name, acc);
    }
    return acc;
  }

}
