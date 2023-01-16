package org.rescript.pom;

public class DependencyOptions {

  private static ThreadLocal<Boolean> options = new ThreadLocal<>();

  public static void setQuiet(boolean quiet) {
    options.set(quiet);
  }

  public static boolean isQuiet() {
    Boolean b = options.get();
    return b!=null?b:false;
  }

}
