package org.ria.run;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

import org.ria.ScriptException;
import org.ria.parser.FunctionParameter;
import org.ria.symbol.java.RUtils;
import org.ria.value.ObjValue;
import org.ria.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaConstructorCaller {

  private static final Logger log = LoggerFactory.getLogger(JavaConstructorCaller.class);

  private ScriptContext ctx;

  public JavaConstructorCaller(ScriptContext ctx) {
    super();
    this.ctx = ctx;
  }

  public Value call(String type, List<FunctionParameter> plist) {
    Class<?> cls = ctx.getSymbols().getJavaSymbols().resolveType(type);
    if(cls == null) {
      throw new ScriptException("class not found " + type);
    }
    log.debug("found '{}' for type '{}'", cls.getName(), type);
    return call(cls, plist);
  }

  public Value call(Class<?> cls, List<FunctionParameter> plist) {
    try {
      Value[] parameters = resolveParameters(plist, ctx);
      Constructor<?> c = RUtils.matchSignature(parameters, List.of(cls.getConstructors()), ctx);
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

}
