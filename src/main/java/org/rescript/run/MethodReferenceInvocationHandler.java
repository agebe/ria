package org.rescript.run;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

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
    List<Method> methods = RUtils.findAccessibleMethods(
        methodValue.getTargetType(), methodValue.getTarget(), methodValue.getMethodName());
    // TODO selecting a method by number of parameters is probably not good enough
    Method me = methods.stream()
        .filter(m -> m.getParameterCount() == args.length)
        .findFirst()
        .orElse(null);
    return me.invoke(methodValue.getTarget(), args);
  }

}
