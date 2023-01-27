package org.ria;

public class Features {

  public boolean javaSourceEnabled = true;

  public boolean dependenciesEnabled = true;

  public boolean loopsEnabled = true;

  public boolean isJavaSourceEnabled() {
    return javaSourceEnabled;
  }

  public Features setJavaSourceEnabled(boolean javaSourceEnabled) {
    this.javaSourceEnabled = javaSourceEnabled;
    return this;
  }

  public boolean isDependenciesEnabled() {
    return dependenciesEnabled;
  }

  public Features setDependenciesEnabled(boolean dependenciesEnabled) {
    this.dependenciesEnabled = dependenciesEnabled;
    return this;
  }

  public boolean isLoopsEnabled() {
    return loopsEnabled;
  }

  public Features setLoopsEnabled(boolean loopsEnabled) {
    this.loopsEnabled = loopsEnabled;
    return this;
  }

}
