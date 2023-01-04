package org.rescript.java;

import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class JavaSource extends SimpleJavaFileObject {

  final String sourceCode;

  final String name;

  public JavaSource(String name, String sourceCode) {
    super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension), Kind.SOURCE);
    this.sourceCode = sourceCode;
    this.name = name;
  }

  @Override
  public CharSequence getCharContent(boolean ignoreEncodingErrors) {
    return sourceCode;
  }

  public String getName() {
    return name;
  }
}
