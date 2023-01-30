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

import java.lang.reflect.Constructor;

public class ConstructorValue implements Value {

  private Class<?> targetType;

  private int dim;

  public ConstructorValue(Class<?> targetType, int dim) {
    super();
    this.targetType = targetType;
    this.dim = dim;
  }

  @Override
  public Class<?> type() {
    return Constructor.class;
  }

  @Override
  public String typeOf() {
    return "constructor";
  }

  @Override
  public Object val() {
    return null;
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

  public Class<?> getTargetType() {
    return targetType;
  }

  public int getDim() {
    return dim;
  }

  @Override
  public boolean isConstructor() {
    return true;
  }

  @Override
  public ConstructorValue toConstructorValue() {
    return this;
  }

}
