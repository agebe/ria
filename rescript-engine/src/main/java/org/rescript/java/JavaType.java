package org.rescript.java;

public enum JavaType {
  CLASS,
  INTERFACE,
  ENUM,
  RECORD,
  ANNOTATION,
  ;

  public String code() {
    if(ANNOTATION.equals(this)) {
      return "@interface";
    } else {
      return toString().toLowerCase();
    }
  }

}
