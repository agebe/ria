package org.rescript.run;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.rescript.ScriptException;
import org.rescript.symbol.java.RUtils;
import org.rescript.value.MethodValue;

public class MethodReferenceInvocationHandler implements InvocationHandler {

  private MethodValue methodValue;

//  private ScriptContext ctx;

  public MethodReferenceInvocationHandler(MethodValue value, ScriptContext ctx) {
    super();
    this.methodValue = value;
//    this.ctx = ctx;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    final int paramCount = args!=null?args.length:0;
    List<Method> methods = RUtils.findAccessibleMethods(
        methodValue.getTargetType(), methodValue.getTarget(), methodValue.getMethodName());
    return methods.stream()
        .filter(m -> m.getParameterCount() == paramCount)
     // FIXME also check that the parameter types match
        .findFirst()
        .orElseThrow(() -> new ScriptException(
            "method with parameter types '%s' not found on class '%s'".formatted(
                toTypeString(args), methodValue.getTargetType().getName())))
        .invoke(methodValue.getTarget(), args);
  }

  private String toTypeString(Object[] args) {
    return Arrays.stream(args)
        .map(a -> a!=null?a.getClass().getName():"null")
        .collect(Collectors.joining(", "));
  }

}
