package org.rescript.launcher;

import java.io.File;
import java.nio.file.Files;

import org.rescript.Script;

public class ScriptLauncher {

  private static String[] scriptArgs(String[] args) {
    String[] scriptArgs = new String[args.length-1];
    System.arraycopy(args, 1, scriptArgs, 0, scriptArgs.length);
    return scriptArgs;
  }

  public static void main(String[] args) {
    //System.out.println(Arrays.toString(args));
    if(args.length < 2) {
      System.err.println("script file parameter missing");
      System.exit(1);
    }
//    String rescriptHome = args[0];
    String scriptFile = args[1];
    try {
      File f = new File(scriptFile);
      if(f.exists()) {
        String script = new String(Files.readAllBytes(f.toPath()));
        Script s = new Script(script);
        s.setShowErrorsOnConsole(true);
        s.setArguments(scriptArgs(args));
        s.run();
      } else {
        System.err.println("script file '%s' not found".formatted(f.getAbsolutePath()));
        System.exit(1);
      }
    } catch(Throwable t) {
      t.printStackTrace();
      System.exit(1);
    }
  }

}
