package org.rescript.expression;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.rescript.ScriptException;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

public class NewArrayOp implements Expression {

  private String type;

  private List<Expression> dimensions;

  public NewArrayOp(String type, List<Expression> dimensions) {
    super();
    this.type = type;
    this.dimensions = dimensions;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    checkDimensions(dimensions);
    Class<?> cls = ctx.getSymbols().getJavaSymbols().resolveType(type);
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
