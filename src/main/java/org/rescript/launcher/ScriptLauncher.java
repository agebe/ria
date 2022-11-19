package org.rescript.launcher;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.rescript.Script;

public class ScriptLauncher {

  public static void main(String[] args) throws Exception {
    if(args.length == 1) {
      File f = new File(args[0]);
      if(f.exists()) {
        String script = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
        new Script(script).run();
      }
    } else {
      System.out.println("script launcher");
      System.out.println("number of arguments '%s'".formatted(args.length));
      for(int i=0;i<args.length;i++) {
        System.out.println(args[i]);
      }
      System.out.println("done");
    }
  }

}
