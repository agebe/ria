package org.rescript.statement;

import org.rescript.ScriptException;
import org.rescript.expression.Assignment;
import org.rescript.expression.Identifier;
import org.rescript.parser.Type;
import org.rescript.run.ScriptContext;
import org.rescript.value.ArrayValue;
import org.rescript.value.BooleanValue;
import org.rescript.value.ByteValue;
import org.rescript.value.CharValue;
import org.rescript.value.DoubleValue;
import org.rescript.value.FloatValue;
import org.rescript.value.IntValue;
import org.rescript.value.LongValue;
import org.rescript.value.ObjValue;
import org.rescript.value.ShortValue;
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
