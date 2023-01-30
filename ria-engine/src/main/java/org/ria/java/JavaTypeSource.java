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
package org.ria.java;

import org.apache.commons.lang3.StringUtils;
import org.ria.ScriptException;
import org.ria.expression.Expression;
import org.ria.run.ScriptContext;
import org.ria.value.Value;

public class JavaTypeSource implements JavaSourceBuilder {

  private Expression expression;

  public JavaTypeSource(Expression expression) {
    this.expression = expression;
  }

  @Override
  public JavaSource create(ScriptContext ctx) {
    Value v = expression.eval(ctx);
    String s = (String)v.val();
    return new JavaSource(getName(s), s);
  }

  private String getName(String source) {
    // FIXME class, interface keywords etc could appear in strings of type annotations
    String[] split = StringUtils.split(source);
    for(int i = 0;i<split.length;i++) {
      String s = split[i];
      if("class".equals(s)) {
        return removeGenericType(split[i+1]);
      } else if("interface".equals(s)) {
        return removeGenericType(split[i+1]);
      } else if("enum".equals(s)) {
        return removeGenericType(split[i+1]);
      } else if("record".equals(s)) {
        return removeParams(removeGenericType(split[i+1]));
      } else if("@interface".equals(s)) {
        return split[i+1];
      }
    }
    throw new ScriptException("could not determine name of java source " + source);
  }

  private String removeGenericType(String name) {
    int i = StringUtils.indexOf(name, "<");
    return i==-1?name:StringUtils.substring(name, 0, i);
  }

  private String removeParams(String name) {
    int i = StringUtils.indexOf(name, "(");
    return i==-1?name:StringUtils.substring(name, 0, i);
  }

}
