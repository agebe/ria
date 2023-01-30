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

import org.ria.ScriptException;

public class UnresolvedIdentifier implements Value {

  private String identifier;

  public UnresolvedIdentifier(String identifier) {
    super();
    this.identifier = identifier;
  }

  @Override
  public Class<?> type() {
    throw new ScriptException("unresolved identifier '%s'".formatted(identifier));
  }

  @Override
  public String typeOf() {
    return "undefined";
  }

  @Override
  public Object val() {
    throw new ScriptException("unresolved identifier '%s'".formatted(identifier));
  }

  @Override
  public boolean isPrimitive() {
    return false;
  }

  @Override
  public boolean equalsOp(Value other) {
    return false;
  }

  public String getIdentifier() {
    return identifier;
  }

}
