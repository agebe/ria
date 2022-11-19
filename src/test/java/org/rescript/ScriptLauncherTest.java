package org.rescript;

import org.rescript.launcher.ScriptLauncher;

public class ScriptLauncherTest {

  public static void main(String[] args) throws Exception {
    // FIXME why is the script executed twice??? 
    ScriptLauncher.main(new String[] {"/home/andre/script/local/hello.bs"});
  }

}
