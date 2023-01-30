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

import java.util.List;
import java.util.Objects;

import org.ria.statement.Function;

public class FunctionValue implements Value {

  private List<Function> functions;

  public FunctionValue(List<Function> functions) {
    super();
    this.functions = functions;
  }

  @Override
  public Class<?> type() {
    return Function.class;
  }

  @Override
  public String typeOf() {
    return "function";
  }

  @Override
  public Object val() {
    return functions;
  }

  @Override
  public boolean isPrimitive() {
    return false;
  }

  @Override
  public boolean equalsOp(Value other) {
    if(other == null) {
      return false;
    } else if(other.isFunction()) {
     return Objects.equals(val(), other.val());
    } else {
      return false;
    }
  }

  public List<Function> getFunctions() {
    return functions;
  }

  @Override
  public boolean isFunction() {
    return true;
  }

  @Override
  public FunctionValue toFunctionValue() {
    return this;
  }

}
