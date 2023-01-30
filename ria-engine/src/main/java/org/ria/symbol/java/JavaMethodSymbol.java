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
