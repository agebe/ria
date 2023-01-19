package org.rescript.launcher;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class CliOptions {

  public boolean version;

  public boolean downloadDependenciesOnly;

  public boolean debug;

  public boolean info;

  public boolean quiet;

  public boolean help;

  public String mavenRepo;

  public Path scriptEngineHome;

  public Path scriptFile;

  public String[] scriptArgs;

  public CliOptions(String[] args) {
    scriptEngineHome = Paths.get(args[0]);
    for(int i=1;i<args.length;i++) {
      String s = args[i];
      if(equalsAny(s, "--version", "-v")) {
        version = true;
      } else if(equalsAny(s, "--download-dependencies-only", "-d")) {
        downloadDependenciesOnly = true;
      } else if(equalsAny(s, "--maven-repository", "-r")) {
        i++;
        mavenRepo = args[i];
      } else if(equalsAny(s, "--debug")) {
        debug = true;
      } else if(equalsAny(s, "--info")) {
        info = true;
      } else if(equalsAny(s, "--quiet", "-q")) {
        quiet = true;
      } else if(equalsAny(s, "--home")) {
        // ignore, this is passed as argument 0 from the native launcher
        i++;
      } else if(equalsAny(s, "--help", "-h")) {
        help = true;
      } else {
        scriptFile = Paths.get(args[i]);
        int remainingArgs = args.length - (i+1);
        if(remainingArgs > 0) {
          scriptArgs = new String[remainingArgs];
          System.arraycopy(args, i+1, scriptArgs, 0, remainingArgs);
        } else {
          scriptArgs = new String[0];
        }
        break;
      }
    }
  }

  public void printHelp() {
    System.out.println("""
        Usage: ria [ options ... ] script-file script-arguments
        where options include:
          --version, -v                     display version
          --download-dependencies-only, -d  download missing dependencies into the cache and exit
          --maven-repository, -r            the default maven repo to use if not specified in the script
          --home                            set script engine home, e.g. maven download cache ('%s')
          --debug                           display debug output
          --info                            display info output on std error
          --quiet, -q                       display less output on std error
          --help, -h                        display this help and exit
        """.formatted(scriptEngineHome));
  }

  private boolean equalsAny(String s, String... others) {
    for(String o : others) {
      if(s.equals(o)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return "CliOptions [version=" + version + ", downloadDependenciesOnly=" + downloadDependenciesOnly + ", debug="
        + debug + ", info=" + info + ", quiet=" + quiet + ", help=" + help + ", mavenRepo=" + mavenRepo
        + ", scriptEngineHome=" + scriptEngineHome + ", scriptFile=" + scriptFile + ", scriptArgs="
        + Arrays.toString(scriptArgs) + "]";
  }

}
