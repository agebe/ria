package org.rescript.run;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.rescript.ScriptException;
import org.rescript.expression.FunctionCall;
import org.rescript.parser.FunctionParameter;
import org.rescript.symbol.java.JavaMethodSymbol;
import org.rescript.symbol.java.RUtils;
import org.rescript.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaFunctionCaller {

  private static final Logger log = LoggerFactory.getLogger(JavaFunctionCaller.class);

  private ScriptContext ctx;

  public JavaFunctionCaller(ScriptContext ctx) {
    super();
    this.ctx = ctx;
  }

  public Value call(FunctionCall function, Value target) {
    try {
      if(target != null) {
        log.debug("calling function '{}' on target '{}'", function.getName().getName(), target);
        JavaMethodSymbol symbol = new JavaMethodSymbol(
            target.type(),
            function.getName().getName(),
            target.val());
        return callJavaMethod(symbol, function);
      } else {
        log.debug("calling java function '{}'", function.getName().getName());
        JavaMethodSymbol symbol = ctx.getSymbols().getJavaSymbols().resolveFunction(function.getName().getName());
        if(symbol != null) {
          return callJavaMethod(symbol, function);
        } else {
          throw new ScriptException("function '%s' not found".formatted(function.getName().getName()));
        }
      }
    } catch(Exception e) {
      throw new ScriptException("function '%s' failed on target '%s'".formatted(function, target), e);
    }
  }

  private Value callJavaMethod(JavaMethodSymbol symbol, FunctionCall fcall) {
    // fcall function name might have been replaced via alias
    String fname = symbol.getMethodName();
    Value[] parameters = resolveParameters(fcall.getParameters());
    log.debug("function parameters '{}'", Arrays.toString(parameters));
    Object[] params = Arrays.stream(parameters).map(Value::val).toArray();
    Class<?>[] paramTypes = Arrays.stream(parameters).map(Value::type).toArray(Class[]::new);
    Class<?> cls = symbol.getTargetType();
    Method m = RUtils.matchSignature(paramTypes, RUtils.findAccessibleMethods(cls, symbol.getTarget(), fname));
    if(m != null) {
      try {
        log.debug("invoke method '{}' with parameter types '{}', '{}'",
            m.getName(),
            Arrays.toString(paramTypes),
            Arrays.toString(params));
        Object result = m.invoke(symbol.getTarget(), RUtils.prepareParamsForInvoke(m, parameters, ctx));
        Class<?> returnType = m.getReturnType();
        return Value.of(returnType, result);
      } catch(InvocationTargetException e) {
        throw new ScriptException("function '%s' exception".formatted(fname), e);
      } catch(IllegalAccessException e) {
        throw new ScriptException("function '%s' illegal access".formatted(fname), e);
      }
    } else {
      throw new ScriptException("method '%s' with parameter types '%s' not found on target '%s'"
          .formatted(fname, Arrays.toString(paramTypes), cls.getName()));
    }
  }

  private Value[] resolveParameters(List<FunctionParameter> parameters) {
    return parameters.stream()
        .map(p -> p.getParameter().eval(ctx))
        .toArray(Value[]::new);
  }

}
