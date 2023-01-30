package org.ria.run;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.ria.ScriptException;
import org.ria.expression.CastOp;
import org.ria.parser.Type;
import org.ria.symbol.java.RUtils;
import org.ria.value.MethodValue;
import org.ria.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodReferenceInvocationHandler implements InvocationHandler {

  private static final Logger log = LoggerFactory.getLogger(MethodReferenceInvocationHandler.class);

  private MethodValue methodValue;

  private ScriptContext ctx;

  public MethodReferenceInvocationHandler(MethodValue value, ScriptContext ctx) {
    super();
    this.methodValue = value;
    this.ctx = ctx;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    ClassLoader ctxLoader = Thread.currentThread().getContextClassLoader();
    try {
      Thread.currentThread().setContextClassLoader(ctx.getSymbols().getJavaSymbols().getClassLoader());
      return tryInvoke(proxy, method, args);
    } finally {
      Thread.currentThread().setContextClassLoader(ctxLoader);
    }
  }

  private Object tryInvoke(Object proxy, Method method, Object[] args) throws Throwable {
    List<Method> methods = RUtils.findAccessibleMethods(
        methodValue.getTargetType(), methodValue.getTarget(), methodValue.getMethodName());
    Method m = chooseMethod(methods, args);
    if(m != null) {
      try {
        return ctx.getFirewall().checkAndInvoke(m, methodValue.getTarget(), castAll(m, args));
      } catch(IllegalArgumentException e) {
        String expected = Arrays.stream(m.getParameterTypes())
            .map(cls -> cls.getName())
            .collect(Collectors.joining(", "));
        throw new ScriptException("invoke method '%s' failed with illegal arguments, passed '%s', expected '%s'"
            .formatted(m.getName(), toTypeString(args), expected));
      }
    } else {
      // https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html
      // Reference to an Instance Method of an Arbitrary Object of a Particular Type
      return invokeOnParam0(method, args);
    }
  }

  private Object invokeOnParam0(Method method, Object[] args) {
    if((args != null) && (args.length > 0)) {
      Object target = args[0];
      if(!methodValue.getTargetType().isAssignableFrom(target.getClass())) {
        throw new ScriptException("method '%s' on type '%s' not compatible to target type '%s'"
            .formatted(methodValue.getMethodName(), methodValue.getTargetType(), target.getClass()));
      }
      Object[] newArgs = new Object[args.length-1];
      System.arraycopy(args, 1, newArgs, 0, newArgs.length);
      List<Method> methods = RUtils.findAccessibleMethods(
          target.getClass(), target, methodValue.getMethodName());
      Method m = chooseMethod(methods, newArgs);
      return ctx.getFirewall().checkAndInvoke(m, target, castAll(m, newArgs));
    } else {
      throw new ScriptException("method not found '%s', params '%s'".formatted(method.getName(), toTypeString(args)));
    }
  }

  private Object[] castAll(Method m, Object[] args) {
    if(args == null) {
      return null;
    }
    if(m.getParameterCount() == args.length) {
      Object[] result = new Object[args.length];
      for(int i=0;i<args.length;i++) {
        result[i] = CastOp.castTo(Value.of(args[i]), new Type(m.getParameterTypes()[i]), ctx).val();
      }
      return result;
    } else if(m.isVarArgs()) {
      Object[] result = new Object[m.getParameterCount()];
      for(int i=0;i<m.getParameterCount()-1;i++) {
        result[i] = CastOp.castTo(Value.of(args[i]), new Type(m.getParameterTypes()[i]), ctx).val();
      }
      Class<?> varargType = m.getParameterTypes()[m.getParameterCount()-1];
      Object[] arr = (Object[])Array.newInstance(varargType.componentType(), 0);
      result[m.getParameterCount()-1] = arr;
      for(int pi=0,i=(m.getParameterCount()-1);i<args.length;i++,pi++) {
        Array.set(arr, pi, args);
      }
      log.debug("varargs parameter types '{}'", toTypeString(result));
      return result;
    } else {
      throw new ScriptException("wrong number of parameters on method '%s', supplied parameters '%s'"
          .formatted(m.getName(), toTypeString(args)));
    }
  }

  private Class<?>[] types(Object[] args) {
    return Arrays.stream(args)
        .map(o -> o!=null?o.getClass():null)
        .toArray(Class[]::new);
  }

  private Method chooseMethod(List<Method> methods, Object[] args) {
    return methods
        .stream()
        .filter(m -> matchesParams(m, args))
        .findFirst()
        .orElse(null);
  }

  private boolean matchesParams(Method m, Object[] args) {
    if(m.isVarArgs()) {
      return matchesParamsVarargsMethod(m, args);
    } else {
      return matchesParamsRegularMethod(m, args);
    }
  }

  private boolean matchesParamsVarargsMethod(Method m, Object[] args) {
    Class<?>[] methodParams = m.getParameterTypes();
    Class<?>[] argsClasses = types(args);
    // check params before the vararg first
    for(int i=0;i<methodParams.length-1;i++) {
      if(args.length < (i+1)) {
        return false;
      }
      Class<?> mp = methodParams[i];
      Class<?> pt = argsClasses[i];
      if(pt == null) {
        continue;
      }
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
    for(int i=methodParams.length;i<args.length;i++) {
      Class<?> pt = argsClasses[i];
      if(pt == null) {
        continue;
      }
      if(!varargType.isAssignableFrom(pt)) {
        return false;
      }
    }
    log.debug("match parameters varargs success '{}', method parameter type '{}', supplied types '{}'",
        m.getName(), Arrays.toString(m.getParameterTypes()), Arrays.toString(argsClasses));
    return true;
  }

  @SuppressWarnings("rawtypes")
  private boolean matchesParamsRegularMethod(Method m, Object[] args) {
    List<Class> types = args != null?Arrays.stream(args).map(this::getType).toList():Collections.emptyList();
    if(m.getParameterCount() != types.size()) {
      log.debug("method '{}' parameter count mismatch, expected '{}', supplied '{}'",
          m.getName(),
          m.getParameterCount(),
          types.size());
      return false;
    }
    if(m.getParameterCount() == 0 && types.isEmpty()) {
      return true;
    }
    if(m.getParameterCount() != types.size()) {
      return false;
    }
    for(int i=0;i<m.getParameterTypes().length;i++) {
      Class<?> mpt = m.getParameterTypes()[i];
      Class<?> at = types.get(i);
      Value v = Value.of(at, args[i]);
      try {
        Value cast = CastOp.castTo(v, new Type(mpt), ctx);
        if(!mpt.isAssignableFrom(cast.type())) {
          log.debug("filter out method '{}', parameter '{}' type '{}' is not assignable from supplied type '{}'",
              m.getName(),
              i,
              mpt,
              at);
          return false;
        }
      } catch(Exception e) {
        log.debug("filter out method '{}', parameter '{}' type '{}' can not cast supplied type '{}'",
            m.getName(),
            i,
            mpt,
            at);
        return false;
      }
    }
    return true;
  }

  @SuppressWarnings("rawtypes")
  private Class getType(Object o) {
    return o!=null?o.getClass():Object.class;
  }

  private String toTypeString(Object[] args) {
    return Arrays.stream(args)
        .map(a -> a!=null?a.getClass().getName():"null")
        .collect(Collectors.joining(", "));
  }

}
