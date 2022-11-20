package org.rescript.statement;

import org.rescript.ScriptException;
import org.rescript.expression.Assignment;
import org.rescript.expression.CastOp;
import org.rescript.expression.Identifier;
import org.rescript.run.ScriptContext;
import org.rescript.symbol.SymbolNotFoundException;
import org.rescript.value.BooleanValue;
import org.rescript.value.CharValue;
import org.rescript.value.DoubleValue;
import org.rescript.value.FloatValue;
import org.rescript.value.IntValue;
import org.rescript.value.LongValue;
import org.rescript.value.ObjValue;
import org.rescript.value.Value;

public class VarDef {

  private Identifier ident;

  private Assignment assign;

  public VarDef(Identifier ident) {
    super();
    this.ident = ident;
  }

  public VarDef(Assignment assign) {
    super();
    this.assign = assign;
  }

  public void execute(ScriptContext ctx, String type) {
    if(ident != null) {
      ctx.getSymbols().getScriptSymbols().defineVar(ident.getIdent(), defaultValue(ctx, type));
    } else if(assign != null) {
      assign.identifiers().forEach(i -> ctx.getSymbols().getScriptSymbols().defineVar(i.getIdent(), null));
      Value v = assign.eval(ctx);
      ctx.setLastResult(v);
      if(type != null) {
        castAll(ctx, type);
      }
    } else {
      throw new ScriptException("invalid state, ident and assign null");
    }
  }

  private Value castAll(ScriptContext ctx, String type) {
    Value[] last = new Value[1];
    assign.identifiers().forEach(i -> {
      Value v = ctx.getSymbols().getScriptSymbols().getVariable(i.getIdent());
      Value castValue = new CastOp(type, c -> v).eval(ctx);
      last[0] = castValue;
      ctx.getSymbols().getScriptSymbols().assignVar(i.getIdent(), castValue);
    });
    return last[0];
  }

  private Value defaultValue(ScriptContext ctx, String type) {
    if(type == null) {
      return ObjValue.NULL;
    } else if(type.equals("double")) {
      return new DoubleValue(0);
    } else if(type.equals("float")) {
      return new FloatValue(0);
    } else if(type.equals("long")) {
      return new LongValue(0);
    } else if(type.equals("int")) {
      return new IntValue(0);
    } else if(type.equals("char")) {
      return new CharValue(0);
    } else if(type.equals("byte")) {
      throw new ScriptException("byte value not implemented");
    } else if(type.equals("short")) {
      throw new ScriptException("short value not implemented");
    } else if(type.equals("boolean")) {
      return BooleanValue.FALSE;
    } else {
      Class<?> cls = ctx.getSymbols().getJavaSymbols().resolveType(type);
      if(cls == null) {
        throw new SymbolNotFoundException("type '%s' could not be resolved".formatted(type));
      }
      return new ObjValue(cls, null);
    }
  }

}
