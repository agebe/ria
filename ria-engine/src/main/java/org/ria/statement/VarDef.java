package org.ria.statement;

import org.ria.ScriptException;
import org.ria.expression.Assignment;
import org.ria.expression.Identifier;
import org.ria.parser.Type;
import org.ria.run.ScriptContext;
import org.ria.value.ArrayValue;
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

  public void execute(ScriptContext ctx, Type type) {
    // type can be null if e.g. the variable was declared via 'var' terminal.
    // in this case the variable can freely change it's type (as opposed to java)
    // otherwise the type is fixed and can't change over it's lifetime (same as in java)
    if(ident != null) {
      ctx.getSymbols().getScriptSymbols().defineVar(ident.getIdent(), defaultValue(ctx, type), type);
    } else if(assign != null) {
      assign.identifiers().forEach(
          i -> ctx.getSymbols().getScriptSymbols().defineVar(i.getIdent(), defaultValue(ctx, type), type));
      Value v = assign.eval(ctx);
      ctx.setLastResult(v);
    } else {
      throw new ScriptException("invalid state, ident and assign null");
    }
  }

  private Value defaultValue(ScriptContext ctx, Type type) {
    // FIXME add primitive arrays
    if(type == null) {
      return ObjValue.NULL;
    } else if(type.isDouble()) {
      return new DoubleValue(0);
    } else if(type.isFloat()) {
      return new FloatValue(0);
    } else if(type.isLong()) {
      return new LongValue(0);
    } else if(type.isInt()) {
      return new IntValue(0);
    } else if(type.isChar()) {
      return new CharValue((char)0);
    } else if(type.isByte()) {
      return new ByteValue((byte)0);
    } else if(type.isShort()) {
      return new ShortValue((short)0);
    } else if(type.isBoolean()) {
      return BooleanValue.FALSE;
    } else {
      Class<?> cls = type.resolve(ctx);
      if(cls == null) {
        return ObjValue.NULL;
      } else if(cls.isArray()) {
        return new ArrayValue(null, cls);
      } else {
        return new ObjValue(cls, null);
      }
    }
  }

  @Override
  public String toString() {
    return "VarDef [ident=" + ident + ", assign=" + assign + "]";
  }

}
