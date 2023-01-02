package org.rescript.java;

public interface JavaSourceBuilder {

  void addImport(String imp);

  void addStaticImport(String staticImport);

  JavaSource create();

}
