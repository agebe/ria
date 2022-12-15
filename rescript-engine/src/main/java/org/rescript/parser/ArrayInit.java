package org.rescript.parser;

import java.util.List;

public class ArrayInit implements ParseItem {

  private List<ParseItem> arrayInitializers;

  public ArrayInit(List<ParseItem> arrayInitializers) {
    super();
    this.arrayInitializers = arrayInitializers;
  }

  public List<ParseItem> getArrayInitializers() {
    return arrayInitializers;
  }

}
