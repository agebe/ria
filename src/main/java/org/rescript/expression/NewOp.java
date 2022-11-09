package org.rescript.expression;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

import org.rescript.ScriptException;
import org.rescript.parser.FunctionParameter;
import org.rescript.run.ScriptContext;
import org.rescript.symbol.java.RUtils;
import org.rescript.value.ObjValue;
import org.rescript.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewOp implements Expression {

  private static final Logger log = LoggerFactory.getLogger(NewOp.class);

  private String type;

  private List<FunctionParameter> plist;

  public NewOp(String type, List<FunctionParameter> parameters) {
    super();
    this.type = type;
    this.plist = parameters;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    try {
      Value[] parameters = resolveParameters(plist, ctx);
      Class<?> cls = ctx.getSymbols().getJavaSymbols().resolveType(type);
      if(cls == null) {
        throw new ScriptException("class not found " + type);
      }
      log.debug("found '{}' for type '{}'", cls.getName(), type);
      Constructor<?> c = RUtils.matchSignature(parameters, List.of(cls.getConstructors()));
      if( c == null) {
        throw new ScriptException("no constructor matching parameters found " + Arrays.toString(parameters));
      }
      log.debug("using constructor " + c);
      Object o = c.newInstance(RUtils.prepareParamsForInvoke(c, parameters, ctx));
      return new ObjValue(cls, o);
    } catch(Exception e) {
      // FIXME improve message
      throw new ScriptException("failed on '%s'".formatted(toString()), e);
    }
  }

  private Value[] resolveParameters(List<FunctionParameter> parameters, ScriptContext ctx) {
    return parameters.stream()
        .map(p -> p.getParameter().eval(ctx))
        .toArray(Value[]::new);
  }

  @Override
  public String getText() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String toString() {
    return "NewOp [type=" + type + ", parameters=" + plist + "]";
  }

}
