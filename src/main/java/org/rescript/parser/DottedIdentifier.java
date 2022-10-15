package org.rescript.parser;

public class DottedIdentifier implements ParseItem {

  private String ident;

  public DottedIdentifier(String ident) {
    super();
    this.ident = ident;
  }

  public String getIdent() {
    return ident;
  }

}
