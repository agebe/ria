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
    try {
      if(args.length == 1) {
        File f = new File(args[0]);
        if(f.exists()) {
          String script = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
          Script s = new Script(script);
          s.setShowErrorsOnConsole(true);
          s.run();
        }
      } else {
        System.out.println("script launcher");
        System.out.println("number of arguments '%s'".formatted(args.length));
        for(int i=0;i<args.length;i++) {
          System.out.println(args[i]);
        }
        System.out.println("done");
      }
    } catch(Throwable t) {
      String msg = "script failed with exception";
      log.error(msg, t);
      throw new ScriptException(msg, t);
    }
  }

}
