package org.rescript.expression;

import org.rescript.run.ScriptContext;
import org.rescript.symbol.SymbolNotFoundException;
import org.rescript.value.ObjValue;
import org.rescript.value.Value;

public class ClassLiteral implements Expression {

  private org.rescript.parser.Type type;

  public ClassLiteral(org.rescript.parser.Type type) {
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
