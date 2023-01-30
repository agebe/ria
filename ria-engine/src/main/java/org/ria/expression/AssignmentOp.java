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
import org.ria.value.ObjValue;
import org.ria.value.SymbolValue;
import org.ria.value.Value;

public class AssignmentOp implements Assignment {

  private Identifier ident;

  private Expression expression;

  public AssignmentOp(Identifier ident, Expression expression) {
    super();
    this.ident = ident;
    this.expression = expression;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value v = expression.eval(ctx);
    ctx.getSymbols().getScriptSymbols().assignVar(ident.getIdent(), v);
    return v;
  }

  private Field findField(Class<?> cls, Object o, String name) {
    return o!=null?RUtils.findField(cls, name):RUtils.findStaticField(cls, name);
  }

  private Value assign(ScriptContext ctx, ObjValue objValue, Value target, Value v) {
    Class<?> cls = objValue.getType();
    Object o = objValue.getVal();
    Field f = findField(cls, o, ident.getIdent());
    if(f == null) {
      throw new ScriptException("'%s' field '%s' not found on class '%s'"
          .formatted(
              (o!=null?"member":"static"),
              ident.getIdent(),
              cls.getName()));
    }
    new FieldSymbol(f, o, ctx).set(v);
    return v;
  }

  @Override
  public Value eval(ScriptContext ctx, Value target) {
    Value v = expression.eval(ctx);
    if(target instanceof SymbolValue s) {
      Value vObj = s.getSymbol().get();
      if(vObj instanceof ObjValue objValue) {
        return assign(ctx, objValue, target, v);
      }
    } else if(target instanceof ObjValue objValue) {
      return assign(ctx, objValue, target, v);
    }
    throw new ScriptException("field assign failed, name '%s', target '%s'"
        .formatted(ident.getIdent(), target));
  }

  @Override
  public String getText() {
    return ident.getText() + "=" + expression.getText();
  }

  @Override
  public String toString() {
    return "AssignmentOperator [ident=" + ident + ", expression=" + expression + "]";
  }

  @Override
  public List<Identifier> identifiers() {
    return List.of(ident);
  }

}
