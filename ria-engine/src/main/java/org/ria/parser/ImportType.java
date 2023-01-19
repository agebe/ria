package org.ria.parser;

public class ImportType implements ParseItem {

  private String type;

  public ImportType(String type) {
    super();
    this.type = type;
  }

  public String getType() {
    return type;
  }

}
