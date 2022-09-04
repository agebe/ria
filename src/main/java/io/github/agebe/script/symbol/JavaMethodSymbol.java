package io.github.agebe.script.symbol;

import io.github.agebe.script.ScriptException;
import io.github.agebe.script.parser.Result;

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
  public Result toResult() {
    throw new ScriptException("not supported");
  }

}
