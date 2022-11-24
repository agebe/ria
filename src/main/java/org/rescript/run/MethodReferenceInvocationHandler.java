package org.rescript.run;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.rescript.ScriptException;
import org.rescript.expression.CastOp;
import org.rescript.symbol.java.RUtils;
import org.rescript.value.MethodValue;
import org.rescript.value.Value;
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
    List<Method> methods = RUtils.findAccessibleMethods(
        methodValue.getTargetType(), methodValue.getTarget(), methodValue.getMethodName());
    Method m = chooseMethod(methods, args);
    if(m != null) {
      try {
        return m.invoke(methodValue.getTarget(), castAll(m, args));
      } catch(IllegalArgumentException e) {
        String expected = Arrays.stream(m.getParameterTypes())
            .map(cls -> cls.getName())
            .collect(Collectors.joining(", "));
        throw new ScriptException("invoke method '%s' failed with illegal arguments, passed '%s', expected '%s'"
            .formatted(m.getName(), toTypeString(args), expected));
      }
    } else {
      throw new ScriptException("none of the '%s' '%s' methods available matches input argument types '%s'".formatted(
          methods.size(), method.getName(), toTypeString(args)));
    }
  }

  private Object[] castAll(Method m, Object[] args) {
    if(args == null) {
      return null;
    }
    Object[] result = new Object[args.length];
    for(int i=0;i<args.length;i++) {
      result[i] = CastOp.castTo(Value.of(args[i]), m.getParameterTypes()[i].getName(), ctx).val();
    }
    return result;
  }

  private Method chooseMethod(List<Method> methods, Object[] args) {
    final int paramCount = args!=null?args.length:0;
    return methods
        .stream()
        .filter(m -> m.getParameterCount() == paramCount)
        .filter(m -> matchesParams(m, args))
        .findFirst()
        .orElse(null);
  }

  @SuppressWarnings("rawtypes")
  private boolean matchesParams(Method m, Object[] args) {
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
    for(int i=0;i<m.getParameterTypes().length;i++) {
      Class<?> mpt = m.getParameterTypes()[i];
      Class<?> at = types.get(i);
      Value v = Value.of(at, args[i]);
      try {
        Value cast = CastOp.castTo(v, mpt.getName(), ctx);
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
