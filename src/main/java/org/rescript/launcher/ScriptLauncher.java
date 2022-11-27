package org.rescript.launcher;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.rescript.Script;
import org.rescript.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptLauncher {

  private static final Logger log = LoggerFactory.getLogger(ScriptLauncher.class);

  public static void main(String[] args) {
    if(args.length == 0) {
      System.err.println("script file parameter missing");
      System.exit(1);
    }
    try {
      File f = new File(args[0]);
      if(f.exists()) {
        String script = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
        Script s = new Script(script);
        s.setShowErrorsOnConsole(true);
        s.setArguments(args);
        s.run();
      } else {
        System.err.println("script file '%s' not found".formatted(f.getAbsolutePath()));
        System.exit(1);
      }
    } catch(Throwable t) {
      String msg = "script failed with exception";
      log.error(msg, t);
      throw new ScriptException(msg, t);
    }
  }

}
