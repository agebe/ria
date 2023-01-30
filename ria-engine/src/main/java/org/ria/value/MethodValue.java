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
