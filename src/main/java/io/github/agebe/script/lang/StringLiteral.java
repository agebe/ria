package io.github.agebe.script.lang;

import io.github.agebe.script.LangType;
import io.github.agebe.script.Value;

public class StringLiteral extends CachedLangItem {

  private String literal;

  public StringLiteral(String literal) {
    super();
    this.literal = literal;
  }

  public String getLiteral() {
    return literal;
  }

  @Override
  public String toString() {
    return "StringLiteral [literal=" + literal + "]";
  }

  @Override
  protected Result resolveOnce() {
    return new Result(LangType.OBJ, Value.ofObj(literal, String.class));
  }

}
