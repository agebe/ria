package io.github.agebe.script.symbol;

import io.github.agebe.script.LangType;
import io.github.agebe.script.Value;
import io.github.agebe.script.parser.Result;

public class VarSymbol implements Symbol {

  private String name;

  private LangType type;

  private Value val;

  public VarSymbol(String name, LangType type, Value val) {
    super();
    this.name = name;
    this.type = type;
    this.val = val;
  }

  public String getName() {
    return name;
  }

  public LangType getType() {
    return type;
  }

  public Value getVal() {
    return val;
  }

  @Override
  public Result toResult() {
    return new Result(type, val);
  }

}
