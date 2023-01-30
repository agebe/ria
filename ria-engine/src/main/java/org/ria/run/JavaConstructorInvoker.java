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
package org.ria.run;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

import org.ria.ScriptException;
import org.ria.parser.FunctionParameter;
import org.ria.symbol.java.RUtils;
import org.ria.value.ObjValue;
import org.ria.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaConstructorInvoker {

  private static final Logger log = LoggerFactory.getLogger(JavaConstructorInvoker.class);

  private ScriptContext ctx;

  public JavaConstructorInvoker(ScriptContext ctx) {
    super();
    this.ctx = ctx;
  }

  public Value invoke(String type, List<FunctionParameter> plist) {
    Class<?> cls = ctx.getSymbols().getJavaSymbols().resolveType(type);
    if(cls == null) {
      throw new ScriptException("class not found " + type);
    }
    log.debug("found '{}' for type '{}'", cls.getName(), type);
    return invoke(cls, plist);
  }

  public Value invoke(Class<?> cls, List<FunctionParameter> plist) {
    Value[] parameters = resolveParameters(plist, ctx);
    Constructor<?> c = RUtils.matchSignature(parameters, List.of(cls.getConstructors()), ctx);
    if( c == null) {
      throw new ScriptException("no constructor matching parameters found " + Arrays.toString(parameters));
    }
    log.debug("using constructor " + c);
    Object o = ctx.getFirewall().checkAndInvoke(c, RUtils.prepareParamsForInvoke(c, parameters, ctx));
    return new ObjValue(cls, o);
  }

  private Value[] resolveParameters(List<FunctionParameter> parameters, ScriptContext ctx) {
    return parameters.stream()
        .map(p -> p.getParameter().eval(ctx))
        .toArray(Value[]::new);
  }

}
