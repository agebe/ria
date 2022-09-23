package org.rescript.run;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.rescript.ScriptException;
import org.rescript.expression.FunctionCall;
import org.rescript.parser.FunctionParameter;
import org.rescript.symbol.JavaMethodSymbol;
import org.rescript.symbol.RUtils;
import org.rescript.value.BooleanValue;
import org.rescript.value.DoubleValue;
import org.rescript.value.FloatValue;
import org.rescript.value.ObjValue;
import org.rescript.value.Value;
import org.rescript.value.VoidValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionCaller {

  private static final Logger log = LoggerFactory.getLogger(FunctionCaller.class);

  private ScriptContext ctx;

  public FunctionCaller(ScriptContext ctx) {
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
        JavaMethodSymbol symbol = ctx.getSymbols().resolveFunction(function.getName().getName());
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
        Object result = m.invoke(symbol.getTarget(), RUtils.prepareParamsForInvoke(m, params));
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
          if(result != null) {
            return new ObjValue(result.getClass(), result);
          } else {
            return new ObjValue(returnType, null);
          }
        }
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
