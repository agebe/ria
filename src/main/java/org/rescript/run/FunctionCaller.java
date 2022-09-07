package org.rescript.run;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.rescript.ScriptException;
import org.rescript.parser.FunctionCall;
import org.rescript.parser.FunctionParameter;
import org.rescript.symbol.JavaMethodSymbol;
import org.rescript.symbol.SymbolTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionCaller {

  private static final Logger log = LoggerFactory.getLogger(FunctionCaller.class);

  private SymbolTable symbols;

  private Expressions expressions;

  public FunctionCaller(SymbolTable symbols, Expressions expressions) {
    this.symbols = symbols;
    this.expressions = expressions;
  }

  public Value call(FunctionCall function) {
    try {
      if(function.getTarget() != null) {
        Value target = function.getTarget().eval(expressions);
        JavaMethodSymbol symbol = new JavaMethodSymbol(
            target.type(),
            function.getName().getName(),
            target.val());
        return callJavaMethod(symbol, function);
      } else {
        JavaMethodSymbol symbol = symbols.resolveFunction(function.getName().getName());
        if(symbol != null) {
          return callJavaMethod(symbol, function);
        } else {
          throw new ScriptException("function '%s' not found".formatted(function.getName().getName()));
        }
      }
    } catch(Exception e) {
      throw new ScriptException("script execution failed", e);
    }
  }

  private Value callJavaMethod(JavaMethodSymbol symbol, FunctionCall fcall) throws Exception {
    // fcall function name might have been replaced via alias
    String fname = symbol.getMethodName();
    Value[] parameters = resolveParameters(fcall.getParameters());
    Object[] params = Arrays.stream(parameters).map(Value::val).toArray();
    Class<?>[] paramTypes = Arrays.stream(parameters).map(Value::type).toArray(Class[]::new);
    Class<?> cls = symbol.getTargetType();
    Method m = matchMethod(findMethods(cls, fname), paramTypes);
    if(m != null) {
      log.debug("invoke method '{}' with parameter types '{}'", m.getName(), Arrays.toString(paramTypes));
      Object result = m.invoke(symbol.getTarget(), params);
      Class<?> returnType = m.getReturnType();
      if(returnType.equals(Void.class) || returnType.equals(void.class)) {
        return new VoidValue();
      } else if(returnType.isPrimitive()) {
        if(returnType.getName().equals("boolean")) {
          return new BooleanValue(result);
        } else if(returnType.getName().equals("double")) {
          return new DoubleValue(result);
        } else if(returnType.getName().equals("float")) {
          return new FloatValue(result);
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
        return new ObjValue(returnType, result);
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
        .orElseGet(() -> matchMethodFuzzy(methods, paramTypes));
  }

  private boolean matchesParametersExactly(Method m, Class<?>[] paramTypes) {
    if(m.getParameterTypes().length != paramTypes.length) {
      return false;
    }
    for(int i=0;i<paramTypes.length;i++) {
      Class<?> mp = m.getParameterTypes()[i];
      Class<?> pt = paramTypes[i];
      if(!Objects.equals(mp, pt)) {
        log.debug("match parameters exactly failed '{}', method parameter type '{}', supplied types '{}'",
            m.getName(), Arrays.toString(m.getParameterTypes()), Arrays.toString(paramTypes));
        return false;
      }
    }
    log.debug("match parameters exactly success '{}', method parameter type '{}', supplied types '{}'",
        m.getName(), Arrays.toString(m.getParameterTypes()), Arrays.toString(paramTypes));
    return true;
  }

  private Method matchMethodFuzzy(List<Method> methods, Class<?>[] paramTypes) {
    return methods.stream()
        .filter(m -> matchesMethodFuzzy(m, paramTypes))
        .findFirst()
        .orElse(null);
  }

  private boolean matchesMethodFuzzy(Method m, Class<?>[] paramTypes) {
    Class<?>[] methodParams = m.getParameterTypes();
    if(methodParams.length != paramTypes.length) {
      return false;
    }
    for(int i=0;i<paramTypes.length;i++) {
      Class<?> mp = methodParams[i];
      Class<?> pt = paramTypes[i];
      if(!mp.isAssignableFrom(pt)) {
        log.debug("match parameters fuzzy failed '{}', method parameter type '{}', supplied types '{}'",
            m.getName(), Arrays.toString(m.getParameterTypes()), Arrays.toString(paramTypes));
        return false;
      }
    }
    log.debug("match parameters fuzzy success '{}', method parameter type '{}', supplied types '{}'",
        m.getName(), Arrays.toString(m.getParameterTypes()), Arrays.toString(paramTypes));
    return true;
  }

  private Value[] resolveParameters(List<FunctionParameter> parameters) {
    return parameters.stream()
        .map(p -> p.getParameter().eval(expressions))
        .toArray(Value[]::new);
  }

}
