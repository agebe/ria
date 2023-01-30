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
package org.ria.expression;

import java.util.List;

import org.ria.ScriptException;
import org.ria.parser.FunctionName;
import org.ria.parser.FunctionParameter;
import org.ria.run.ScriptContext;
import org.ria.value.Value;

public class FunctionCall implements TargetExpression {

  private FunctionName name;

  private List<FunctionParameter> parameters;

  public FunctionCall(
      FunctionName name,
      List<FunctionParameter> parameters,
      Expression target) {
    super();
    this.name = name;
    this.parameters = parameters;
  }

  public FunctionName getName() {
    return name;
  }

  public List<FunctionParameter> getParameters() {
    return parameters;
  }

  @Override
  public String toString() {
    return "FunctionCall [name=" + name + ", parameters=" + parameters + "]";
  }

  @Override
  public Value eval(ScriptContext ctx) {
    return ctx.getFunctions().call(this, null);
  }

  @Override
  public Value eval(ScriptContext ctx, Value target) {
    return ctx.getFunctions().call(this, target);
  }

  @Override
  public String getText() {
    throw new ScriptException("not implemented");
  }

}
