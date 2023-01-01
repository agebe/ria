package org.rescript.java;

public interface JavaSourceBuilder {

  void addImport(String importType);

  JavaSource create();

}
