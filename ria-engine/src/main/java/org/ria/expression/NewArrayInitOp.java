package org.ria.expression;

import java.lang.reflect.Array;

import org.ria.ScriptException;
import org.ria.parser.ArrayInit;
import org.ria.parser.ParseItem;
import org.ria.parser.Type;
import org.ria.run.ScriptContext;
import org.ria.value.Value;

public class NewArrayInitOp implements Expression {

  private Type type;

  private ArrayInit init;

  public NewArrayInitOp(Type type, ArrayInit init) {
    super();
    this.type = type;
    this.init = init;
  }

  private Object initArray(ScriptContext ctx, Class<?> cls, int dim, ArrayInit init) {
    if(dim <= 0) {
      throw new ScriptException("invalid array dimension '%s'".formatted(dim));
    }
    Class<?> arrayType = cls;
    for(int i = 0;i<dim-1;i++) {
      arrayType = arrayType.arrayType();
    }
    int size = init.getArrayInitializers().size();
    Object array = Array.newInstance(arrayType, size);
    for(int i=0;i<init.getArrayInitializers().size();i++) {
      ParseItem p = init.getArrayInitializers().get(i);
      if(p instanceof ArrayInit nested) {
        Array.set(array, i, initArray(ctx, cls, dim-1, nested));
      } else if(p instanceof Expression exp) {
        Value v = exp.eval(ctx);
        Array.set(array, i, v.isNotNull()?CastOp.castTo(v, new Type(cls), ctx).val():null);
      } else {
        throw new ScriptException("unexpected array initializer '%s'".formatted(p));
      }
    }
    return array;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Class<?> cls = type.resolveBaseType(ctx);
    Object array = initArray(ctx, cls, type.getDim(), init);
    return ArrayUtil.toArrayValue(array);
  }
}
