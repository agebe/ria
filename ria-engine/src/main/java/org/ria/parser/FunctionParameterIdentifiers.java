package org.ria.parser;

import java.util.List;

import org.ria.expression.Identifier;

public class FunctionParameterIdentifiers implements ParseItem {

  private List<Identifier> identifiers;

  public FunctionParameterIdentifiers(List<Identifier> identifiers) {
    super();
    this.identifiers = identifiers;
  }

  public List<Identifier> getIdentifiers() {
    return identifiers;
  }

}
