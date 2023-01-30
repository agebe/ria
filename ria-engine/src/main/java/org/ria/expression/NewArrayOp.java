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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.ria.ScriptException;
import org.ria.parser.Type;
import org.ria.run.ScriptContext;
import org.ria.value.Value;

public class NewArrayOp implements Expression {

  private Type type;

  private List<Expression> dimensions;

  public NewArrayOp(Type type, List<Expression> dimensions) {
    super();
    this.type = type;
    this.dimensions = dimensions;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    checkDimensions(dimensions);
    Class<?> cls = type.resolveBaseType(ctx);
    int[] dims = dimensions.stream()
        .filter(Objects::nonNull)
        .mapToInt(dim -> dim.eval(ctx).toInt())
        .toArray();
    int emptyDimensions = (int)dimensions.stream()
        .filter(Objects::isNull)
        .count();
    for(int i=0;i<emptyDimensions;i++) {
      cls = cls.arrayType();
    }
    Object array = Array.newInstance(cls, dims);
    try {
      return ArrayUtil.toArrayValue(array);
    } catch(Exception e) {
      throw new ScriptException("new array op failed for type '%s', dimensions '%s'"
          .formatted(type, Arrays.toString(dims)));
    }
  }

  public static void checkDimensions(List<Expression> dimensions) {
    if(dimensions.get(0) == null) {
      throw new ScriptException("first dimension required");
    }
    boolean seenNull = false;
    for(Expression e : dimensions) {
      if((e != null) && seenNull) {
        throw new ScriptException("can not specify array dimension after empty dimension");
      } else if(e == null) {
        seenNull = true;
      }
    }
  }

}
