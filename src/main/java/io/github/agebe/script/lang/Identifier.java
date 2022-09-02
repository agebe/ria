package io.github.agebe.script.lang;

import io.github.agebe.script.SymbolTable;

public class Identifier extends CachedLangItem {

  private String ident;

  private SymbolTable symbols;

  public Identifier(String ident, SymbolTable symbols) {
    super();
    this.ident = ident;
    this.symbols = symbols;
  }

  public String getIdent() {
    return ident;
  }

  @Override
  public String toString() {
    return "Identifier [ident=" + ident + "]";
  }

  @Override
  protected Result resolveOnce() {
    return symbols.resolveAsResult(ident);
  }

}
