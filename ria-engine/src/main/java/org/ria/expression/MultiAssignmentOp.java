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

import java.lang.reflect.Field;
import java.util.List;

import org.ria.ScriptException;
import org.ria.run.ScriptContext;
import org.ria.symbol.java.FieldSymbol;
import org.ria.symbol.java.RUtils;
import org.ria.value.Array;
import org.ria.value.ObjValue;
import org.ria.value.SymbolValue;
import org.ria.value.Value;

public class MultiAssignmentOp implements Assignment {

  private List<Identifier> identifiers;

  private Expression expr;

  public MultiAssignmentOp(List<Identifier> identifiers, Expression expr) {
    super();
    this.identifiers = identifiers;
    this.expr = expr;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value v = expr.eval(ctx);
    // if value is an array or list, assign elements to variables
    // nothing to do if there are not enough variables, extra array elements are ignored in this case
    // if there are to many variables, assign the whole array/list to the each of them
    if(v instanceof Array a) {
      for(int i=0;i<identifiers.size();i++) {
        ctx.getSymbols().getScriptSymbols().assignVar(identifiers.get(i).getIdent(),
            (i<a.length()?a.get(i):v));
      }
    } else if(v.val() instanceof List<?> l) {
      for(int i=0;i<identifiers.size();i++) {
        ctx.getSymbols().getScriptSymbols().assignVar(identifiers.get(i).getIdent(),
            (i<l.size()?Value.of(l.get(i)):v));
      }
    } else {
      for(int i=0;i<identifiers.size();i++) {
        ctx.getSymbols().getScriptSymbols().assignVar(identifiers.get(i).getIdent(), v);
      }
    }
    return v;
  }

  @Override
  public List<Identifier> identifiers() {
    return identifiers;
  }

  private Field findField(Class<?> cls, Object o, String name) {
    return o!=null?RUtils.findField(cls, name):RUtils.findStaticField(cls, name);
  }

  private Field findFieldNotNull(Class<?> cls, Object o, String name) {
    Field f = findField(cls, o, name);
    if(f == null) {
      throw new ScriptException("'%s' field '%s' not found on class '%s'"
          .formatted(
              (o!=null?"member":"static"),
              name,
              cls.getName()));
    }
    return f;
  }

  private Value assign(ScriptContext ctx, ObjValue objValue, Value target, Value v) {
    Class<?> cls = objValue.getType();
    Object o = objValue.getVal();
    if(v instanceof Array a) {
      for(int i=0;i<identifiers.size();i++) {
        String ident = identifiers.get(i).getIdent();
        Field f = findFieldNotNull(cls, o, ident);
        new FieldSymbol(f, o, ctx).set(v);
      }
    } else if(v.val() instanceof List<?> l) {
      for(int i=0;i<identifiers.size();i++) {
        String ident = identifiers.get(i).getIdent();
        Field f = findFieldNotNull(cls, o, ident);
        new FieldSymbol(f, o, ctx).set((i<l.size()?Value.of(l.get(i)):v));
      }
    } else {
      for(int i=0;i<identifiers.size();i++) {
        String ident = identifiers.get(i).getIdent();
        Field f = findFieldNotNull(cls, o, ident);
        new FieldSymbol(f, o, ctx).set(v);
      }
    }
    return v;
  }

  @Override
  public Value eval(ScriptContext ctx, Value target) {
    Value v = expr.eval(ctx);
    if(target instanceof SymbolValue s) {
      Value vObj = s.getSymbol().get();
      if(vObj instanceof ObjValue objValue) {
        return assign(ctx, objValue, target, v);
      }
    } else if(target instanceof ObjValue objValue) {
      return assign(ctx, objValue, target, v);
    }
    throw new ScriptException("field multi assign failed, names '%s', target '%s'"
        .formatted(identifiers, target));
  }

}
