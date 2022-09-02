package io.github.agebe.script;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.github.agebe.script.lang.FunctionCall;
import io.github.agebe.script.lang.FunctionParameter;
import io.github.agebe.script.lang.Result;

public class FunctionCaller {

  private SymbolTable symbols;

  public FunctionCaller(SymbolTable symbols) {
    this.symbols = symbols;
  }

  public Result call(FunctionCall function) {
    try {
      if(function.getTarget() != null) {
        Result target = function.getTarget().resolve();
        return callJavaMethod(target, function);
      }
      throw new ScriptException("not impl");
    } catch(Exception e) {
      throw new ScriptException("script execution failed", e);
    }
  }

  private Result callJavaMethod(Result target, FunctionCall fcall) throws Exception {
    String fname = fcall.getName().getName();
    Object[] params = resolveParameters(fcall.getParameters());
    Class<?>[] paramTypes = resolveParameterTypes(fcall.getParameters());
    Class<?> cls = target.getValue().getCls();
    Method m = matchMethod(findMethods(cls, fname), paramTypes);
    if(m != null) {
      Object result = m.invoke(target.getValue().getObj(), params);
      Class<?> returnType = m.getReturnType();
      if(returnType.equals(Void.class) || returnType.equals(void.class)) {
        return new Result(LangType.VOID, Value.vd());
      } else if(returnType.isPrimitive()) {
        if(returnType.getName().equals("boolean")) {
          return new Result(LangType.BOOLEAN, Value.ofBoolean(result));
        } else {
          // TODO support all primitive types
          System.out.println(returnType.isPrimitive());
          System.out.println(returnType.getName());
          throw new ScriptException("primitive return type '%s' not impl yet".formatted(returnType.getName()));
        }
      } else if(returnType.isArray()) {
        // TODO
        throw new ScriptException("array not impl yet");
      } else {
        return new Result(LangType.OBJ, Value.ofObj(result, returnType));
      }
    } else {
      throw new ScriptException("method '%s' with parameter types '%s' not found on target '%s'"
          .formatted(fname, Arrays.toString(paramTypes), cls.getName()));
    }
  }

  private List<Method> findMethods(Class<?> cls, String name) {
    return Arrays.stream(cls.getMethods())
        .filter(m -> m.getName().equals(name))
        .toList();
  }

  private Method matchMethod(List<Method> methods, Class<?>[] paramTypes) {
    return methods.stream()
        .filter(m -> matchesParametersExactly(m, paramTypes))
        .findFirst()
        .orElse(matchMethodFuzzy(methods, paramTypes));
  }

  private boolean matchesParametersExactly(Method m, Class<?>[] paramTypes) {
    if(m.getParameterTypes().length != paramTypes.length) {
      return false;
    }
    for(int i=0;i<paramTypes.length;i++) {
      Class<?> mp = m.getParameterTypes()[i];
      Class<?> pt = paramTypes[i];
      if(!Objects.equals(mp, pt)) {
        return false;
      }
    }
    return true;
  }

  private Method matchMethodFuzzy(List<Method> methods, Class<?>[] paramTypes) {
    return methods.stream()
        .filter(m -> matchesMethodFuzzy(m, paramTypes))
        .findFirst()
        .orElse(null);
  }

  private boolean matchesMethodFuzzy(Method m, Class<?>[] paramTypes) {
    if(m.getParameterTypes().length != paramTypes.length) {
      return false;
    }
    for(int i=0;i<paramTypes.length;i++) {
      Class<?> mp = m.getParameterTypes()[i];
      Class<?> pt = paramTypes[i];
      if(!mp.isAssignableFrom(pt)) {
        return false;
      }
    }
    return true;
  }

  private Object[] resolveParameters(List<FunctionParameter> parameters) {
    return parameters.stream()
        .map(p -> p.resolve().getValue().getObj())
        .toArray();
  }

  private Class<?>[] resolveParameterTypes(List<FunctionParameter> parameters) {
    return parameters.stream()
        .map(p -> p.resolve().getValue().paramType())
        .toArray(Class<?>[]::new);
  }

}
