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
package org.ria.parser;

import org.apache.commons.lang3.StringUtils;
import org.ria.run.ScriptContext;
import org.ria.symbol.SymbolNotFoundException;

public class Type {

  private String name;

  // array dimensions
  private int dim;

  private Class<?> cls;

  private boolean val;

  public Type() {
    // -1 dimensions skips the dimensions check below. use for 'var' as it also could be an array
    this(null, -1);
  }

  public Type(boolean val) {
    this(null, -1);
    this.val = val;
  }

  public Type(String name) {
    this(name,0);
  }

  public Type(String name, int dim) {
    super();
    this.name = name;
    this.dim = dim;
  }

  public Type(Class<?> cls) {
    this.cls = cls;
    this.name = cls.getName();
    if(cls.isArray()) {
   // FIXME how to figure out the array dimensions of a given class?
      this.dim = -1;
    } else {
      this.dim = 0;
    }
  }

  public String getName() {
    return name;
  }

  public int getDim() {
    return dim;
  }

  public boolean isArray() {
    return dim > 0;
  }

  public boolean isNotArray() {
    return !isArray();
  }

//  public void checkArrayDimensions(int dimensions) {
//    if(dim == -1) {
//      return;
//    }
//    if(dim != dimensions) {
//      throw new ScriptException(
//          "array dimensions mismatch, type has '%s' array dimensions but array has '%s' dimensions"
//          .formatted(dim, dimensions));
//    }
//  }

  public boolean isPrimitive() {
    return isDouble() ||
        isFloat() ||
        isLong() ||
        isInt() ||
        isBoolean() ||
        isByte() ||
        isChar() ||
        isShort() ||
        isVoid();
  }

  public boolean isDouble() {
    return isNotArray() && "double".equals(name);
  }

  public boolean isFloat() {
    return isNotArray() && "float".equals(name);
  }

  public boolean isLong() {
    return isNotArray() && "long".equals(name);
  }

  public boolean isInt() {
    return isNotArray() && "int".equals(name);
  }

  public boolean isBoolean() {
    return isNotArray() && "boolean".equals(name);
  }

  public boolean isShort() {
    return isNotArray() && "short".equals(name);
  }

  public boolean isChar() {
    return isNotArray() && "char".equals(name);
  }

  public boolean isByte() {
    return isNotArray() && "byte".equals(name);
  }

  public boolean isVoid() {
    return isNotArray() && "void".equals(name);
  }

  public Class<?> resolveBaseType(ScriptContext ctx) {
    if(this.cls != null) {
      return this.cls;
    } else {
      if(StringUtils.isBlank(name)) {
        return null;
      }
      Class<?> cls = ctx.getSymbols().getJavaSymbols().resolveType(name);
      if(cls == null) {
        throw new SymbolNotFoundException("type '%s' could not be resolved".formatted(name));
      }
      return cls;
    }
  }

  public Class<?> resolve(ScriptContext ctx) {
    Class<?> cls = resolveBaseType(ctx);
    if(cls == null) {
      return null;
    }
    for(int i=0;i<dim;i++) {
      cls = cls.arrayType();
    }
    return cls;
  }

  public boolean isVal() {
    return val;
  }

  @Override
  public String toString() {
    return "Type [name=" + name + ", dim=" + dim + ", cls=" + cls + "]";
  }

}
