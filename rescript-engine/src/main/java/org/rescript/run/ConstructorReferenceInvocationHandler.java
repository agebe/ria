package org.rescript.run;

import java.lang.reflect.Array;
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
    if(constructorValue.getDim() == 0) {
      final int paramCount = args!=null?args.length:0;
      return Arrays.stream(constructorValue.getTargetType().getConstructors())
          .filter(c -> c.getParameterCount() == paramCount)
          // FIXME also check that the parameter types match
          .findFirst()
          .orElseThrow(() -> new ScriptException(
              "constructor with parameter types '%s' not found on class '%s'".formatted(
                  toTypeString(args), constructorValue.getTargetType().getName())))
          .newInstance(args);
    } else {
      // create an array of the type and dimensions
      // arguments provide array lengths
      Class<?> arrayType = constructorValue.getTargetType();
      // fix array type if the dimension is > than the provided array lengths
      // so the final array has the required dimensions
      for(int i=0;i<(constructorValue.getDim() - args.length);i++) {
        arrayType = arrayType.arrayType();
      }
      return Array.newInstance(arrayType, arrayLengths(args));
    }
  }

  private int[] arrayLengths(Object[] args) {
    return Arrays.stream(args).mapToInt(o -> (int)o).toArray();
  }

  private String toTypeString(Object[] args) {
    return Arrays.stream(args)
        .map(a -> a!=null?a.getClass().getName():"null")
        .collect(Collectors.joining(", "));
  }

}
