package org.rescript.launcher;

import java.io.File;
import java.nio.file.Files;

import org.rescript.Script;

public class ScriptLauncher {

  public static void main(String[] args) {
    if(args.length == 0) {
      System.err.println("script file parameter missing");
      System.exit(1);
    }
    try {
      File f = new File(args[0]);
      if(f.exists()) {
        String script = new String(Files.readAllBytes(f.toPath()));
        Script s = new Script(script);
        s.setShowErrorsOnConsole(true);
        s.setArguments(args);
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
