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
package org.ria.symbol;

import org.ria.value.MethodValue;
import org.ria.value.Value;

public class ObjectMethodSymbol implements VarSymbol {

  private MethodValue v;

  public ObjectMethodSymbol(MethodValue v) {
    super();
    this.v = v;
  }

  @Override
  public Value get() {
    return v;
  }

  @Override
  public Value inc() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Value dec() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Value getVal() {
    return get();
  }

  @Override
  public Object getObjectOrNull() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setVal(Value val) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void set(Value v) {
    // TODO Auto-generated method stub
    
  }

}
