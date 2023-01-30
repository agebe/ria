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

import org.ria.parser.FunctionParameter;
import org.ria.run.JavaConstructorInvoker;
import org.ria.run.ScriptContext;
import org.ria.util.ExceptionUtils;
import org.ria.value.Value;

public class NewOp implements Expression {

  private String type;

  private List<FunctionParameter> plist;

  public NewOp(String type, List<FunctionParameter> parameters) {
    super();
    this.type = type;
    this.plist = parameters;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value v = new JavaConstructorInvoker(ctx).invoke(type, plist);
    if(v.val() instanceof Throwable t) {
      ExceptionUtils.fixStackTrace(t, ctx);
    }
    return v;
  }

  @Override
  public String toString() {
    return "NewOp [type=" + type + ", parameters=" + plist + "]";
  }

}
