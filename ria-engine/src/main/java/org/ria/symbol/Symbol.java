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

import org.ria.value.Value;

public interface Symbol {

  Value get();

  void set(Value v);

  /**
   * increment number and return new Value or throw exception if operation is not supported or value is not a number
   */
  Value inc();

  /**
   * decrement number and return new Value or throw exception if operation is not supported or value is not a number
   */
  Value dec();

}
