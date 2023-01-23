package org.ria.symbol.java;

import org.ria.ScriptException;
import org.ria.symbol.Symbol;
import org.ria.value.Value;

public class JavaMethodSymbol implements Symbol {

  private Class<?> targetType;

  private String methodName;

  private Object target;

  public JavaMethodSymbol(Class<?> targetType, String methodName, Object target) {
    super();
    this.targetType = targetType;
    this.methodName = methodName;
    this.target = target;
  }

  public Class<?> getTargetType() {
    return targetType;
  }

  public String getMethodName() {
    return methodName;
  }

  public Object getTarget() {
    return target;
  }

  @Override
  public String toString() {
    return "JavaMethodSymbol [targetType=" + targetType + ", methodName=" + methodName + ", target=" + target + "]";
  }

  @Override
  public Value inc() {
    throw new ScriptException("inc not supported");
  }

  @Override
  public Value dec() {
    throw new ScriptException("dec not supported");
  }

  @Override
  public Value get() {
    throw new ScriptException("get not supported");
  }

  @Override
  public void set(Value v) {
    throw new ScriptException("set not supported");
  }

}
