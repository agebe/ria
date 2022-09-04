package io.github.agebe.script.symbol;

import io.github.agebe.script.LangType;
import io.github.agebe.script.Value;
import io.github.agebe.script.parser.Result;

public class ClassSymbol implements Symbol {

  private Class<?> cls;

  public ClassSymbol(Class<?> cls) {
    super();
    this.cls = cls;
  }

  public Class<?> getCls() {
    return cls;
  }

  @Override
  public String toString() {
    return "ClassSymbol [cls=" + cls + "]";
  }

  @Override
  public Result toResult() {
    return new Result(LangType.CLASS, Value.cls(cls));
  }

}
