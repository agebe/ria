package org.rescript.launcher;

public class ScriptLauncher {

  public static void main(String[] args) {
    System.out.println("script launcher");
    System.out.println("number of arguments '%s'".formatted(args.length));
    for(int i=0;i<args.length;i++) {
      System.out.println(args[i]);
    }
    System.out.println("done");
  }

}
