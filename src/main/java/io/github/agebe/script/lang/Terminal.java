package io.github.agebe.script.lang;

import org.antlr.v4.runtime.Token;

public class Terminal extends CachedLangItem {

  private Token token;

  public Terminal(Token token) {
    super();
    this.token = token;
  }

  public Token getToken() {
    return token;
  }

  @Override
  public String toString() {
    return "terminal '%s'".formatted(token.getText());
  }

}
