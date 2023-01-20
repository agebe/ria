package org.ria.run;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.ria.ScriptException;
import org.ria.expression.FunctionCall;
import org.ria.parser.FunctionParameter;
import org.ria.symbol.java.JavaMethodSymbol;
import org.ria.symbol.java.RUtils;
import org.ria.util.ExceptionUtils;
import org.ria.value.MethodValue;
import org.ria.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaMethodInvoker {

  private static final Logger log = LoggerFactory.getLogger(JavaMethodInvoker.class);

  private ScriptContext ctx;

  public JavaMethodInvoker(ScriptContext ctx) {
    super();
    this.ctx = ctx;
  }

  public Value invoke(FunctionCall function, Value target) {
    if(target != null) {
      log.debug("calling function '{}' on target '{}'", function.getName().getName(), target);
      JavaMethodSymbol symbol = new JavaMethodSymbol(
          target.type(),
          function.getName().getName(),
          target.val());
      return invokeJavaMethod(symbol, function);
    } else {
      log.debug("calling java function '{}'", function.getName().getName());
      JavaMethodSymbol symbol = ctx.getSymbols().getJavaSymbols().resolveFunction(function.getName().getName());
      if(symbol != null) {
        return invokeJavaMethod(symbol, function);
      } else {
        throw new ScriptException("function '%s' not found".formatted(function.getName().getName()));
      }
    }
  }

  public Value invoke(MethodValue method, FunctionCall fcall) {
    return invokeJavaMethod(new JavaMethodSymbol(
        method.getTargetType(),
        method.getMethodName(),
        method.getTarget()),
        resolveParameters(fcall.getParameters()));
  }

  public Value invoke(MethodValue method, Value[] values) {
    return invokeJavaMethod(new JavaMethodSymbol(
        method.getTargetType(),
        method.getMethodName(),
        method.getTarget()),
        values);
  }

  private Value invokeJavaMethod(JavaMethodSymbol symbol, FunctionCall fcall) {
    return invokeJavaMethod(symbol, resolveParameters(fcall.getParameters()));
  }

  private Value invokeJavaMethod(JavaMethodSymbol symbol, Value[] parameters) {
    String fname = symbol.getMethodName();
    log.debug("function parameters '{}'", Arrays.toString(parameters));
    Object[] params = Arrays.stream(parameters).map(Value::val).toArray();
    Class<?>[] paramTypes = Arrays.stream(parameters).map(Value::type).toArray(Class[]::new);
    Class<?> cls = symbol.getTargetType();
    Method m = RUtils.matchSignature(parameters, RUtils.findAccessibleMethods(cls, symbol.getTarget(), fname), ctx);
    if(m != null) {
      try {
        log.debug("invoke method '{}' with parameter types '{}', '{}'",
            m,
            Arrays.toString(paramTypes),
            Arrays.toString(params));
        Object result = m.invoke(symbol.getTarget(), RUtils.prepareParamsForInvoke(m, parameters, ctx));
        if(result != null) {
          return Value.of(result.getClass(), result);
        } else {
          Class<?> returnType = m.getReturnType();
          return Value.of(returnType, result);
        }
      } catch(InvocationTargetException e) {
        Throwable t = e.getCause();
        if(t != null) {
          ExceptionUtils.wrapCheckedAndThrow(ExceptionUtils.fixStackTrace(t, ctx));
          // we should never get here but if we do just throw an exception
          throw new ScriptException("exception in function '%s'".formatted(fname), e);
        } else {
          throw new ScriptException(
              "exception in function '%s' with message '%s'".formatted(fname, e.getMessage()), e);
        }
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
