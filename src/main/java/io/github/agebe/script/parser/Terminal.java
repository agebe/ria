package io.github.agebe.script.parser;

import org.antlr.v4.runtime.Token;

public class Terminal implements ParseItem {

  private Token token;

  public Terminal(Token token) {
    super();
    this.token = token;
  }

  public Token getToken() {
    return token;
  }

  public String getText() {
    return token.getText();
  }

  @Override
  public String toString() {
    return "terminal '%s'".formatted(token.getText());
  }

}
