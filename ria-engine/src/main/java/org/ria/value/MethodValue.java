package org.ria.value;

import java.lang.reflect.Method;

import org.apache.commons.lang3.ObjectUtils;

public class MethodValue implements Value {

  private Class<?> targetType;

  private Object target;

  private String methodName;

  public MethodValue(Class<?> targetType, Object target, String methodName) {
    super();
    this.targetType = targetType;
    this.target = target;
    this.methodName = methodName;
  }

  @Override
  public Class<?> type() {
    return Method.class;
  }

  @Override
  public String typeOf() {
    return "method";
  }

  @Override
  public Object val() {
    return "method %s::%s, %s".formatted(
        targetType.getName(),
        methodName,
        target!=null?("on object " + ObjectUtils.identityToString(target)):"static");
  }

  @Override
  public boolean isPrimitive() {
    return false;
  }

  @Override
  public boolean equalsOp(Value other) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isMethod() {
    return true;
  }

  public Object getTarget() {
    return target;
  }

  @Override
  public MethodValue toMethodValue() {
    return this;
  }

  public Class<?> getTargetType() {
    return targetType;
  }

  public String getMethodName() {
    return methodName;
  }

}
