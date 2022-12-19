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
      return new ObjValue(cls, cls); 
    } else {
      throw new SymbolNotFoundException("type '%s'".formatted(type.getName()));
    }
//    Value v = expression.eval(ctx);
//    return new ObjValue(v.type(), v.type());
  }

//  @Override
//  public Value eval(ScriptContext ctx, Value target) {
//    if(target instanceof UnresolvedIdentifier u && expression instanceof Identifier i) {
//      String type = u.getIdentifier()+"."+i.getIdent();
//      Class<?> cls = ctx.getSymbols().getJavaSymbols().resolveType(type);
//      if(cls != null) {
//        return new ObjValue(cls, cls);
//      } else {
//        throw new SymbolNotFoundException("failed to resolve '%s'".formatted(type));
//      }
//    } else {
//      throw new ScriptException("failed to .class on target '%s', expression '%s'".formatted(target, expression));
//    }
//  }

}
