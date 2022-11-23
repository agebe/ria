package org.rescript.expression;

import org.rescript.ScriptException;
import org.rescript.parser.TypeOrPrimitive;
import org.rescript.run.ScriptContext;
import org.rescript.symbol.SymbolNotFoundException;
import org.rescript.value.BooleanValue;
import org.rescript.value.Value;

public class InstanceOfOp implements Expression {

  private Expression expr1;

  private TypeOrPrimitive type;

  private Identifier bind;

  public InstanceOfOp(Expression expr1, TypeOrPrimitive type, Identifier bind) {
    super();
    this.expr1 = expr1;
    this.type = type;
    this.bind = bind;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    boolean isInstanceOf = false;
    Value v1 = expr1.eval(ctx);
    if(v1 == null) {
      throw new ScriptException("left operand failed to evaluate '%s'".formatted(expr1.getText()));
    }
    if(type.isPrimitive()) {
      if(type.isDouble()) {
        isInstanceOf = v1.type() == double.class;
      } else if(type.isFloat()) {
        isInstanceOf = v1.type() == float.class;
      } else if(type.isLong()) {
        isInstanceOf = v1.type() == long.class;
      } else if(type.isInt()) {
        isInstanceOf = v1.type() == int.class;
      } else if(type.isBoolean()) {
        isInstanceOf = v1.type() == boolean.class;
      } else if(type.isChar()) {
        isInstanceOf = v1.type() == char.class;
      } else if(type.isByte()) {
        isInstanceOf = v1.type() == byte.class;
      } else if(type.isShort()) {
        isInstanceOf = v1.type() == short.class;
      } else if(type.isVoid()) {
        isInstanceOf = v1.type() == void.class;
      } else {
        throw new ScriptException("unknown primitive type '%s'".formatted(type.getType()));
      }
    } else {
      Class<?> cls = ctx.getSymbols().getJavaSymbols().resolveType(type.getType());
      if(cls == null) {
        throw new SymbolNotFoundException("type '%s' not found".formatted(type.getType()));
      }
      isInstanceOf = cls.isAssignableFrom(v1.type());
    }
    if(isInstanceOf && (this.bind != null)) {
      ctx.getSymbols().getScriptSymbols().defineVar(this.bind.getIdent(), v1);
    }
    return BooleanValue.of(isInstanceOf);
  }

}
