package org.ria.expression;

import org.ria.run.ScriptContext;
import org.ria.symbol.SymbolNotFoundException;
import org.ria.value.ObjValue;
import org.ria.value.Value;

public class ClassLiteral implements Expression {

  private org.ria.parser.Type type;

  public ClassLiteral(org.ria.parser.Type type) {
    super();
    this.type = type;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Class<?> cls = type.resolve(ctx);
    if(cls != null) {
      return new ObjValue(cls.getClass(), cls);
    } else {
      throw new SymbolNotFoundException("type '%s'".formatted(type.getName()));
    }
  }

}
