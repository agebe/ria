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
package org.ria.statement;

import org.ria.expression.Expression;

// added the yield statement for convenience so it can appear in switch expressions
// (but can also be used anywhere else where statements can appear)
// it only executes the expression and stores the result in the script context.
public class YieldStatement extends ExpressionStatement {

  public YieldStatement(int lineNumber, Expression expression) {
    super(lineNumber, expression);
  }

  @Override
  public String toString() {
    return "YieldStatement []";
  }

}
