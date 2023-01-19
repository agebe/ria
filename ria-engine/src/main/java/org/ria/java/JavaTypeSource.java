package org.ria.java;

import org.apache.commons.lang3.StringUtils;
import org.ria.ScriptException;
import org.ria.expression.Expression;
import org.ria.run.ScriptContext;
import org.ria.value.Value;

public class JavaTypeSource implements JavaSourceBuilder {

  private Expression expression;

  public JavaTypeSource(Expression expression) {
    this.expression = expression;
  }

  @Override
  public JavaSource create(ScriptContext ctx) {
    Value v = expression.eval(ctx);
    String s = (String)v.val();
    return new JavaSource(getName(s), s);
  }

  private String getName(String source) {
    // FIXME class, interface keywords etc could appear in strings of type annotations
    String[] split = StringUtils.split(source);
    for(int i = 0;i<split.length;i++) {
      String s = split[i];
      if("class".equals(s)) {
        return removeGenericType(split[i+1]);
      } else if("interface".equals(s)) {
        return removeGenericType(split[i+1]);
      } else if("enum".equals(s)) {
        return removeGenericType(split[i+1]);
      } else if("record".equals(s)) {
        return removeParams(removeGenericType(split[i+1]));
      } else if("@interface".equals(s)) {
        return split[i+1];
      }
    }
    throw new ScriptException("could not determine name of java source " + source);
  }

  private String removeGenericType(String name) {
    int i = StringUtils.indexOf(name, "<");
    return i==-1?name:StringUtils.substring(name, 0, i);
  }

  private String removeParams(String name) {
    int i = StringUtils.indexOf(name, "(");
    return i==-1?name:StringUtils.substring(name, 0, i);
  }

}
