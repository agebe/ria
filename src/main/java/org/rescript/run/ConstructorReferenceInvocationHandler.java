package org.rescript.run;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.rescript.ScriptException;
import org.rescript.value.ConstructorValue;

public class ConstructorReferenceInvocationHandler implements InvocationHandler {

  private ConstructorValue constructorValue;

//  private ScriptContext ctx;

  public ConstructorReferenceInvocationHandler(ConstructorValue value, ScriptContext ctx) {
    super();
    this.constructorValue = value;
//    this.ctx = ctx;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    final int paramCount = args!=null?args.length:0;
    return Arrays.stream(constructorValue.getTargetType().getConstructors())
        .filter(c -> c.getParameterCount() == paramCount)
        // FIXME also check that the parameter types match
        .findFirst()
        .orElseThrow(() -> new ScriptException(
            "constructor with parameter types '%s' not found on class '%s'".formatted(
                toTypeString(args), constructorValue.getTargetType().getName())))
        .newInstance(args);
  }

  private String toTypeString(Object[] args) {
    return Arrays.stream(args)
        .map(a -> a!=null?a.getClass().getName():"null")
        .collect(Collectors.joining(", "));
  }

}
