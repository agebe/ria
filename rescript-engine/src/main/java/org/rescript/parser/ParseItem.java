package org.rescript.parser;

public interface ParseItem {
  default String getText() {
    // TODO remove default implementation
    return "";
  }
}
