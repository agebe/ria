/*
 * Copyright 2023 Andre Gebers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ria.run;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.ria.ScriptException;
import org.ria.value.ConstructorValue;

public class ConstructorReferenceInvocationHandler implements InvocationHandler {

  private ConstructorValue constructorValue;

  private ScriptContext ctx;

  public ConstructorReferenceInvocationHandler(ConstructorValue value, ScriptContext ctx) {
    super();
    this.constructorValue = value;
    this.ctx = ctx;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if(constructorValue.getDim() == 0) {
      final int paramCount = args!=null?args.length:0;
      Constructor<?> constructor = Arrays.stream(constructorValue.getTargetType().getConstructors())
          .filter(c -> c.getParameterCount() == paramCount)
          // FIXME also check that the parameter types match
          .findFirst()
          .orElseThrow(() -> new ScriptException(
              "constructor with parameter types '%s' not found on class '%s'".formatted(
                  toTypeString(args), constructorValue.getTargetType().getName())));
      return ctx.getFirewall().checkAndInvoke(constructor, args);
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
