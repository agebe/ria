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

import org.ria.ScriptException;
import org.ria.parser.Type;
import org.ria.run.ScriptContext;
import org.ria.value.BooleanValue;
import org.ria.value.ByteValue;
import org.ria.value.CharValue;
import org.ria.value.DoubleValue;
import org.ria.value.FloatValue;
import org.ria.value.IntValue;
import org.ria.value.LongValue;
import org.ria.value.ObjValue;
import org.ria.value.ShortValue;
import org.ria.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CastOp implements Expression {

  private static final Logger log = LoggerFactory.getLogger(CastOp.class);

  private Type type;

  private Expression expression;

  public CastOp(Type type, Expression expression) {
    super();
    this.type = type;
    this.expression = expression;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    return castTo(expression.eval(ctx), type, ctx);
  }

  public static Value castTo(Value v, Type type, ScriptContext ctx) throws ClassCastException {
    if(type.isArray()) {
      if((v!=null) && (v.isArray())) {
        return v;
      } else {
        // TODO should the interpreter automatically convert a scalar value into an array of size 1 on cast?
        log.debug("casting value '{}' to array type '{}' not implemented", v, type);
      }
      return v;
    } else {
      log.debug("cast value '{}' to type '{}'", v, type);
    }
    try {
      // TODO check if value is already of correct type and return v without creating a new value
      if(type.isDouble()) {
        return new DoubleValue(v.toDouble());
      } else if(type.isFloat()) {
        return new FloatValue(v.toFloat());
      } else if(type.isLong()) {
        return new LongValue(v.toLong());
      } else if(type.isInt()) {
        return new IntValue(v.toInt());
      } else if(type.isChar()) {
        return new CharValue(v.toChar());
      } else if(type.isByte()) {
        return new ByteValue(v.toByte());
      } else if(type.isShort()) {
        return new ShortValue(v.toShort());
      } else if(type.isBoolean()) {
        return new BooleanValue(v.toBoolean());
      } else {
        Class<?> cls = type.resolve(ctx);
        if(cls == null) {
          return v!=null?v:ObjValue.NULL;
        } else if(cls == Double.class) {
          return new ObjValue(cls, v.isNotNull()?v.toDouble():null);
        } else if(cls == Float.class) {
          return new ObjValue(cls, v.isNotNull()?v.toFloat():null);
        } else if(cls == Long.class) {
          return new ObjValue(cls, v.isNotNull()?v.toLong():null);
        } else if(cls == Integer.class) {
          return new ObjValue(cls, v.isNotNull()?v.toInt():null);
        } else if(cls == Character.class) {
          return new ObjValue(cls, v.isNotNull()?v.toChar():null);
        } else if(cls == Byte.class) {
          return new ObjValue(cls, v.isNotNull()?v.toByte():null);
        } else if(cls == Short.class) {
          return new ObjValue(cls, v.isNotNull()?v.toShort():null);
        } else if(cls == Boolean.class) {
          return new ObjValue(cls, v.isNotNull()?v.toBoolean():null);
        } else if(cls == String.class) {
          return new ObjValue(cls, v.isNotNull()?v.val().toString():null);
        } else {
          return new ObjValue(cls, cls.cast(v.val()));
        }
      }
    } catch(Exception e) {
      throw new ScriptException("failed to cast '%s' to '%s'".formatted(v.type(), type), e);
    }
  }

  public static boolean canCast(Value v, Type type, ScriptContext ctx) {
    try {
      castTo(v, type, ctx);
      return true;
    } catch(Exception e) {
      return false;
    }
  }
}
