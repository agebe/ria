package io.github.agebe.script.parser;

import io.github.agebe.script.LangType;
import io.github.agebe.script.Value;

public class Result {

  private LangType type;

  private Value value;

  public Result(LangType type, Value value) {
    super();
    this.type = type;
    this.value = value;
  }

  public LangType getType() {
    return type;
  }

  public Value getValue() {
    return value;
  }

}
